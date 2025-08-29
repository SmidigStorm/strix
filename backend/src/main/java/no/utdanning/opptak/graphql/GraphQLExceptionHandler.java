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
          .message("Ingen tilgang til denne ressursen")
          .path(env.getExecutionStepInfo().getPath())
          .location(env.getField().getSourceLocation())
          .build();
    } else if (ex instanceof NullPointerException) {
      return GraphqlErrorBuilder.newError()
          .errorType(ErrorType.NOT_FOUND)
          .message("Ressursen ble ikke funnet")
          .path(env.getExecutionStepInfo().getPath())
          .location(env.getField().getSourceLocation())
          .build();
    } else {
      return GraphqlErrorBuilder.newError()
          .errorType(ErrorType.INTERNAL_ERROR)
          .message("En intern feil oppstod")
          .path(env.getExecutionStepInfo().getPath())
          .location(env.getField().getSourceLocation())
          .build();
    }
  }
}
