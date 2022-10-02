const { ApolloServer, gql } = require('apollo-server');

// A schema is a collection of type definitions (hence "typeDefs")
// that together define the "shape" of queries that are executed against
// your data.
const typeDefs = gql`
  type Query {
    todoRoot: TodoRoot!
  }

  type TodoRoot {
    id: ID!
    all: [Todo!]!
    completed: [Todo!]!
    unCompleted: [Todo!]!
    todo(id: ID!): Todo
  }

  type Todo {
    id: ID!
    title: String!
    done: Boolean!
  }

  type Mutation {
    createTodo(title: String!): TodoRoot!
    toggleDone(id: ID!, done: Boolean!): TodoRoot!
  }
`;

let lastId = 1;
let todos = [
  {
    id: 0,
    title: "foo",
    done: false,
  },
  {
    id: 1,
    title: "bar",
    done: false,
  }
];

// Resolvers define the technique for fetching the types defined in the
// schema. This resolver retrieves books from the "books" array above.

const getTodo = (_, { id }) => {
  const numId = Number(id);
  console.log(id)
  return todos.find(t => t.id === numId);
};

const getTodoRoot = () => {
  const all = todos;
  const completed = todos.filter(t => t.done);
  const unCompleted = todos.filter(t => !t.done);
  return {
    id: "todoRoot",
    all,
    completed,
    unCompleted,
    todo: getTodo,
  };
}

const resolvers = {
  Query: {
    todoRoot: getTodoRoot,
  },
  Mutation: {
    createTodo(_, { title }) {
      const todo = {
        id: lastId + 1,
        title: title,
        done: false,
      };
      todos = [...todos, todo];
      lastId++;
      return getTodoRoot();
    },
    toggleDone(_, { id, done }) {
      const numId = Number(id);
      todos = todos.map(t => {
        if (t.id === numId) {
          t.done = done;
        }
        return t;
      });
      return getTodoRoot();
    },
  },
};

const {
  ApolloServerPluginLandingPageLocalDefault
} = require('apollo-server-core');

// The ApolloServer constructor requires two parameters: your schema
// definition and your set of resolvers.
const server = new ApolloServer({
  typeDefs,
  resolvers,
  csrfPrevention: true,
  cache: 'bounded',
  /**
   * What's up with this embed: true option?
   * These are our recommended settings for using AS;
   * they aren't the defaults in AS3 for backwards-compatibility reasons but
   * will be the defaults in AS4. For production environments, use
   * ApolloServerPluginLandingPageProductionDefault instead.
  **/
  plugins: [
    ApolloServerPluginLandingPageLocalDefault({ embed: true }),
  ],
});

// The `listen` method launches a web server.
server.listen().then(({ url }) => {
  console.log(`🚀  Server ready at ${url}`);
});
