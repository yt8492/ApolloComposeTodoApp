package com.yt8492.apollocomposetodoapp.infra

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.*
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.apollographql.apollo3.cache.normalized.watch
import kotlinx.coroutines.flow.*

@Composable
fun <D: Query.Data> ApolloClient.watch(
    query: Query<D>,
    option: ApolloCall<D>.() -> Unit = {
        fetchPolicy(FetchPolicy.CacheFirst)
    }
): ApolloWatchResult<D> {
    val (result, setResult) = remember(
        key1 = query.name()
    ) {
        mutableStateOf(ApolloResult.empty<D>())
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val fetch = suspend {
        watchAsFlow(query, option)
            .flowWithLifecycle(lifecycleOwner.lifecycle)
            .collect {
                setResult(it)
            }
    }
    val refetch = suspend {
        watchAsFlow(query) {
            fetchPolicy(FetchPolicy.NetworkOnly)
        }
            .flowWithLifecycle(lifecycleOwner.lifecycle)
            .collect {
                setResult(it)
            }
    }
    LaunchedEffect(
        key1 = query.name(),
    ) {
        fetch()
    }
    return ApolloWatchResult(
        data = result.data,
        loading = result.loading,
        error = result.error,
        refetch = refetch,
    )
}

fun <D: Query.Data> ApolloClient.watchAsFlow(
    query: Query<D>,
    option: ApolloCall<D>.() -> Unit = {
        fetchPolicy(FetchPolicy.CacheFirst)
    }
): Flow<ApolloResult<D>> {
    return flow {
        emit(ApolloResult.loadStart())
        emitAll(
            query(query).apply(option)
                .watch()
                .map {
                    ApolloResult.create(it)
                }
                .catch {
                    emit(
                        ApolloResult.create(
                            operation = query,
                            cause = it,
                        )
                    )
                }
        )
    }
}

@Composable
fun <D : Mutation.Data> ApolloClient.mutation(): ApolloMutationWrapper<D> {
    val (result, setResult) = remember {
        mutableStateOf(ApolloResult.empty<D>())
    }
    val mutateFunction: suspend (Mutation<D>) -> Unit = remember {
        { mutation ->
            setResult(ApolloResult.loadStart())
            val res = this@mutation.mutation(mutation)
                .toFlow()
                .map {
                    ApolloResult.create(it)
                }
                .catch {
                    emit(ApolloResult.create(operation = mutation, cause = it))
                }
                .single()
            setResult(res)
        }
    }
    return ApolloMutationWrapper(
        mutateFunction = mutateFunction,
        data = result.data,
        loading = result.loading,
        error = result.error,
    )
}

data class GraphQLError(
    override val cause : Throwable,
    val operationName: String,
    val path: String,
) : Exception(cause) {
    override fun fillInStackTrace() : Throwable = this

    companion object {
        fun createByApolloError(
            apolloError: Error,
            operationName: String,
        ): GraphQLError {
            return GraphQLError(
                cause = Exception(apolloError.message),
                operationName = operationName,
                path = apolloError.path.toString(),
            )
        }
    }
}

data class ApolloError(
    val errors: List<GraphQLError>
) : Exception() {
    override fun fillInStackTrace() : Throwable = this

    override val cause : Throwable?
        get() = errors.firstOrNull()

    override val message : String
        get() = "ApolloError:\n" + errors.joinToString("\n") {
            it.message.toString()
        }

    override fun toString() : String = message

    companion object {
        fun createByGraphQLErrors(
            errors : List<GraphQLError>,
        ): ApolloError {
            return ApolloError(errors = errors)
        }
    }
}

object GraphQLErrorsFactory {
    fun <D : Operation.Data> createByApolloResponse(
        response: ApolloResponse<D>
    ): List<GraphQLError>? {
        return response.errors?.map {
            GraphQLError.createByApolloError(
                apolloError = it,
                operationName = response.operation.name(),
            )
        }
    }
}

@Stable
data class ApolloResult<D>(
    val data: D?,
    val loading: Boolean,
    val error: ApolloError?,
) {
    companion object {
        fun <D> empty(): ApolloResult<D> {
            return ApolloResult(
                data = null,
                loading = false,
                error = null,
            )
        }

        fun <D> loadStart(): ApolloResult<D> {
            return ApolloResult(
                data = null,
                loading = true,
                error = null,
            )
        }

        fun <D : Operation.Data> create(
            response: ApolloResponse<D>,
        ): ApolloResult<D> {
            return ApolloResult(
                data = response.data,
                loading = false,
                error = GraphQLErrorsFactory.createByApolloResponse(response)?.let {
                    ApolloError.createByGraphQLErrors(it)
                },
            )
        }

        fun <D : Operation.Data> create(
            operation: Operation<D>,
            cause : Throwable,
        ): ApolloResult<D> {
            return ApolloResult(
                data = null,
                loading = false,
                error = ApolloError(
                    listOf(
                        GraphQLError(
                            cause = cause,
                            operationName = operation.name(),
                            path = "unknown",
                        ),
                    ),
                ),
            )
        }
    }
}

@Stable
data class ApolloWatchResult<D>(
    val data : D?,
    val loading : Boolean,
    val error: ApolloError?,
    val refetch: suspend () -> Unit,
)

@Stable
data class ApolloMutationWrapper<D : Mutation.Data>(
    val mutateFunction: suspend (Mutation<D>) -> Unit,
    val data: D?,
    val loading: Boolean,
    val error: ApolloError?,
)
