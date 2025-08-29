import { GraphiQL } from 'graphiql';
import 'graphiql/graphiql.css';

const fetcher = async (graphQLParams: any) => {
  const response = await fetch('/graphql', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(graphQLParams),
  });

  return response.json();
};

export function GraphiQLPage() {
  return (
    <div style={{ height: '100vh', width: '100%', all: 'initial', fontFamily: 'system-ui, sans-serif' }}>
      <style>
        {`
          .graphiql-page * {
            all: unset;
            display: revert;
            box-sizing: border-box;
          }
          .graphiql-page {
            height: 100vh;
            width: 100%;
            font-family: system-ui, -apple-system, sans-serif;
          }
        `}
      </style>
      <div className="graphiql-page">
        <GraphiQL fetcher={fetcher} />
      </div>
    </div>
  );
}