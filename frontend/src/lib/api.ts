// API utility functions for GraphQL requests

/**
 * Creates headers for GraphQL requests with optional JWT authentication
 * @param token Optional JWT token for authentication
 * @returns Headers object for fetch requests
 */
export function createGraphQLHeaders(token?: string | null): HeadersInit {
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
  };
  
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  
  return headers;
}

/**
 * Makes a GraphQL request with automatic authentication
 * @param query GraphQL query or mutation string
 * @param variables Optional variables for the GraphQL operation
 * @param token Optional JWT token for authentication
 * @returns Promise with the GraphQL response
 */
export async function graphQLRequest<T = any>(
  query: string,
  variables?: Record<string, any>,
  token?: string | null
): Promise<T> {
  const response = await fetch('/graphql', {
    method: 'POST',
    headers: createGraphQLHeaders(token),
    body: JSON.stringify({
      query,
      variables,
    }),
  });

  const result = await response.json();
  
  if (result.errors) {
    throw new Error(result.errors[0].message);
  }

  return result.data;
}

/**
 * GraphQL mutation for creating an organization
 */
export const CREATE_ORGANISATION_MUTATION = `
  mutation OpprettOrganisasjon($input: OpprettOrganisasjonInput!) {
    opprettOrganisasjon(input: $input) {
      id
      navn
      kortNavn
      type
      organisasjonsnummer
      epost
      telefon
      adresse
      poststed
      postnummer
      nettside
      aktiv
    }
  }
`;

/**
 * GraphQL mutation for updating an organization
 */
export const UPDATE_ORGANISATION_MUTATION = `
  mutation OppdaterOrganisasjon($input: OppdaterOrganisasjonInput!) {
    oppdaterOrganisasjon(input: $input) {
      id
      navn
      kortNavn
      type
      organisasjonsnummer
      epost
      telefon
      adresse
      poststed
      postnummer
      nettside
      aktiv
    }
  }
`;

/**
 * GraphQL mutation for deactivating an organization
 */
export const DEACTIVATE_ORGANISATION_MUTATION = `
  mutation DeaktiverOrganisasjon($id: ID!) {
    deaktiverOrganisasjon(id: $id) {
      id
      aktiv
    }
  }
`;

/**
 * GraphQL query for fetching organizations
 */
export const GET_ORGANISATIONS_QUERY = `
  query GetOrganisasjoner {
    organisasjoner {
      id
      navn
      kortNavn
      type
      organisasjonsnummer
      epost
      telefon
      adresse
      poststed
      postnummer
      nettside
      aktiv
    }
  }
`;

// Utdanning GraphQL operations

/**
 * GraphQL query for fetching utdanninger with pagination
 */
export const GET_UTDANNINGER_QUERY = `
  query GetUtdanninger($filter: UtdanningFilter, $page: PageInput) {
    utdanninger(filter: $filter, page: $page) {
      content {
        id
        navn
        studienivaa
        studiepoeng
        varighet
        studiested
        undervisningssprak
        beskrivelse
        starttidspunkt
        studieform
        aktiv
        organisasjon {
          id
          navn
          kortNavn
        }
      }
      totalElements
      totalPages
      currentPage
      pageSize
      hasNext
      hasPrevious
    }
  }
`;

/**
 * GraphQL query for fetching a single utdanning
 */
export const GET_UTDANNING_QUERY = `
  query GetUtdanning($id: ID!) {
    utdanning(id: $id) {
      id
      navn
      studienivaa
      studiepoeng
      varighet
      studiested
      undervisningssprak
      beskrivelse
      starttidspunkt
      studieform
      aktiv
      organisasjon {
        id
        navn
      }
    }
  }
`;

/**
 * GraphQL mutation for creating utdanning
 */
export const CREATE_UTDANNING_MUTATION = `
  mutation OpprettUtdanning($input: OpprettUtdanningInput!) {
    opprettUtdanning(input: $input) {
      id
      navn
      studienivaa
      studiepoeng
      varighet
      studiested
      undervisningssprak
      beskrivelse
      starttidspunkt
      studieform
      aktiv
      organisasjon {
        id
        navn
      }
    }
  }
`;

/**
 * GraphQL mutation for updating utdanning
 */
export const UPDATE_UTDANNING_MUTATION = `
  mutation OppdaterUtdanning($input: OppdaterUtdanningInput!) {
    oppdaterUtdanning(input: $input) {
      id
      navn
      studienivaa
      studiepoeng
      varighet
      studiested
      undervisningssprak
      beskrivelse
      starttidspunkt
      studieform
      aktiv
      organisasjon {
        id
        navn
      }
    }
  }
`;

/**
 * GraphQL mutation for deactivating utdanning
 */
export const DEACTIVATE_UTDANNING_MUTATION = `
  mutation DeaktiverUtdanning($id: ID!) {
    deaktiverUtdanning(id: $id) {
      id
      aktiv
    }
  }
`;

/**
 * GraphQL mutation for activating utdanning
 */
export const ACTIVATE_UTDANNING_MUTATION = `
  mutation AktiverUtdanning($id: ID!) {
    aktiverUtdanning(id: $id) {
      id
      aktiv
    }
  }
`;

/**
 * GraphQL mutation for deleting utdanning (admin only)
 */
export const DELETE_UTDANNING_MUTATION = `
  mutation SlettUtdanning($id: ID!) {
    slettUtdanning(id: $id)
  }
`;