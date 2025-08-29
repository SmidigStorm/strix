import { createContext, useContext, useState, ReactNode, useEffect } from 'react';
import { useRole } from './RoleContext';

interface User {
  id: string;
  email: string;
  navn: string;
  roller: Array<{
    id: string;
    navn: string;
  }>;
  organisasjon?: {
    id: string;
    navn: string;
  };
}

interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (email: string) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Test-brukere med forhåndsdefinerte passord (test123)
const TEST_USERS = [
  { email: 'admin@strix.no', navn: 'Sara Administrator', rolle: 'Administrator' },
  { email: 'opptaksleder@ntnu.no', navn: 'Kari Opptaksleder', rolle: 'Opptaksleder' },
  { email: 'behandler@uio.no', navn: 'Per Behandler', rolle: 'Søknadsbehandler' },
  { email: 'soker@student.no', navn: 'Astrid Søker', rolle: 'Søker' },
];

interface AuthProviderProps {
  children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const { setSelectedRole } = useRole();

  // Sjekk om bruker allerede er logget inn (fra localStorage)
  useEffect(() => {
    const storedToken = localStorage.getItem('authToken');
    const storedUser = localStorage.getItem('authUser');
    
    if (storedToken && storedUser) {
      setToken(storedToken);
      const parsedUser = JSON.parse(storedUser);
      setUser(parsedUser);
      
      // Sett rolle basert på brukerens faktiske rolle
      const roleFromStorage = parsedUser.roller[0]?.navn || parsedUser.roller[0];
      const userRole = mapBackendRoleToFrontend(roleFromStorage);
      setSelectedRole(userRole);
    }
  }, [setSelectedRole]);

  const mapBackendRoleToFrontend = (backendRole: string): 'Administrator' | 'Opptaksleder' | 'Søknadsbehandler' | 'Søker' => {
    switch (backendRole) {
      case 'ADMINISTRATOR':
      case 'Administrator':
        return 'Administrator';
      case 'OPPTAKSLEDER':
      case 'Opptaksleder':
        return 'Opptaksleder';
      case 'SOKNADSBEHANDLER':
      case 'Søknadsbehandler':
        return 'Søknadsbehandler';
      case 'SOKER':
      case 'Søker':
        return 'Søker';
      default:
        console.warn('Unknown backend role:', backendRole);
        return 'Søker';
    }
  };

  const login = async (email: string) => {
    try {
      // Kall backend login mutation
      const response = await fetch('/graphql', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          query: `
            mutation Login($input: LoginInput!) {
              login(input: $input) {
                token
                bruker {
                  id
                  email
                  navn
                  roller {
                    id
                    navn
                  }
                  organisasjon {
                    id
                    navn
                  }
                }
              }
            }
          `,
          variables: {
            input: {
              email: email,
              passord: 'test123', // Alle test-brukere har samme passord
            }
          },
        }),
      });

      const result = await response.json();
      
      if (result.errors) {
        throw new Error(result.errors[0].message);
      }

      const { token: jwtToken, bruker } = result.data.login;
      
      // Lagre token og brukerinfo
      setToken(jwtToken);
      setUser(bruker);
      localStorage.setItem('authToken', jwtToken);
      localStorage.setItem('authUser', JSON.stringify(bruker));
      
      // Sett rolle basert på brukerens faktiske rolle
      const roleName = bruker.roller[0].navn;
      const userRole = mapBackendRoleToFrontend(roleName);
      setSelectedRole(userRole);
      
    } catch (error) {
      console.error('Login failed:', error);
      throw error;
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('authToken');
    localStorage.removeItem('authUser');
    setSelectedRole('Administrator'); // Reset til default
  };

  return (
    <AuthContext.Provider value={{
      user,
      token,
      login,
      logout,
      isAuthenticated: !!token,
    }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}

// Helper hook for å få test-brukere
export function useTestUsers() {
  return TEST_USERS;
}