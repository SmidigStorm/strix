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
    <div className="h-full w-full">
      <div className="border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60 p-4">
        <h1 className="text-2xl font-semibold">GraphQL Explorer</h1>
        <p className="text-sm text-muted-foreground">
          Utforsk og test GraphQL API-et for Strix opptakssystem
        </p>
      </div>
      <div className="flex-1" style={{ height: 'calc(100vh - 140px)' }}>
        <GraphiQL fetcher={fetcher} />
      </div>
    </div>
  );
}