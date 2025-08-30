package no.utdanning.opptak.graphql;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

  @Override
  protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
    // Log all errors for debugging
    System.err.println("GraphQL Error: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
    ex.printStackTrace();
    
    if (ex instanceof IllegalArgumentException) {
      return GraphqlErrorBuilder.newError()
          .errorType(ErrorType.BAD_REQUEST)
          .message(ex.getMessage())
          .path(env.getExecutionStepInfo().getPath())
          .location(env.getField().getSourceLocation())
          .build();
    } else if (ex instanceof SecurityException) {
      return GraphqlErrorBuilder.newError()
          .errorType(ErrorType.FORBIDDEN)
          .message(ex.getMessage()) // Show actual security error
          .path(env.getExecutionStepInfo().getPath())
          .location(env.getField().getSourceLocation())
          .build();
    } else if (ex instanceof NullPointerException) {
      return GraphqlErrorBuilder.newError()
          .errorType(ErrorType.NOT_FOUND)
          .message(ex.getMessage() != null ? ex.getMessage() : "Ressursen ble ikke funnet")
          .path(env.getExecutionStepInfo().getPath())
          .location(env.getField().getSourceLocation())
          .build();
    } else {
      // Always show the actual error message
      return GraphqlErrorBuilder.newError()
          .errorType(ErrorType.INTERNAL_ERROR)
          .message(ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName())
          .path(env.getExecutionStepInfo().getPath())
          .location(env.getField().getSourceLocation())
          .build();
    }
  }
}
