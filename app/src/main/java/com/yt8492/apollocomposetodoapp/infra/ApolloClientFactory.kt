package com.yt8492.apollocomposetodoapp.infra

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.CompiledField
import com.apollographql.apollo3.api.Executable
import com.apollographql.apollo3.cache.normalized.api.*
import com.apollographql.apollo3.cache.normalized.logCacheMisses
import com.apollographql.apollo3.cache.normalized.normalizedCache

object ApolloClientFactory {
    fun create(): ApolloClient {
        // Programmatic cache IDs in Apollo Kotlin
        // https://www.apollographql.com/docs/kotlin/caching/programmatic-ids
        // https://github.com/apollographql/apollo-kotlin/blob/v3.0.0/tests/integration-tests/src/commonTest/kotlin/Utils.kt
        return ApolloClient.Builder()
            .serverUrl("http://10.0.2.2:4000/")
            .logCacheMisses()
            .normalizedCache(
                normalizedCacheFactory = MemoryCacheFactory(),
                cacheKeyGenerator = object : CacheKeyGenerator {
                    override fun cacheKeyForObject(
                        obj: Map<String, Any?>,
                        context: CacheKeyGeneratorContext
                    ): CacheKey? {
                        return obj["id"]?.toString()?.let { CacheKey(it) }
                            ?: TypePolicyCacheKeyGenerator.cacheKeyForObject(obj, context)
                    }
                },
                cacheResolver = object : CacheResolver {
                    override fun resolveField(
                        field: CompiledField,
                        variables: Executable.Variables,
                        parent: Map<String, Any?>,
                        parentId: String
                    ): Any? {
                        return field.resolveArgument("id", variables)?.toString()
                            ?.let { CacheKey(it) }
                            ?: FieldPolicyCacheResolver.resolveField(field, variables, parent, parentId)
                    }
                }
            )
            .build()
    }
}