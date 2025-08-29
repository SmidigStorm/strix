import { createContext, useContext, useState } from 'react';
import type { ReactNode } from 'react';

export type Role = 'Søker' | 'Søknadsbehandler' | 'Opptaksleder' | 'Administrator';

interface RoleContextType {
  selectedRole: Role;
  setSelectedRole: (role: Role) => void;
  hasPermission: (permission: string) => boolean;
}

const RoleContext = createContext<RoleContextType | undefined>(undefined);

interface RoleProviderProps {
  children: ReactNode;
}

export function RoleProvider({ children }: RoleProviderProps) {
  const [selectedRole, setSelectedRole] = useState<Role>('Søker');

  const hasPermission = (permission: string): boolean => {
    // Basert på tilgangsmatrisen fra requirements/krav/tilgangsstyring/roller-og-tilgang.md
    switch (permission) {
      case 'VIEW_ORGANISATIONS':
        // Alle roller kan se organisasjoner (i ulike nivåer)
        return true;
      
      case 'CREATE_ORGANISATION':
        // Kun Administrator kan opprette organisasjoner
        return selectedRole === 'Administrator';
      
      case 'EDIT_ORGANISATION':
        // Kun Administrator kan redigere alle organisasjoner
        // Opptaksleder kan kun se og redigere egen organisasjonsinformasjon (ikke implementert ennå)
        return selectedRole === 'Administrator';
      
      case 'DELETE_ORGANISATION':
        // Kun Administrator kan deaktivere/slette organisasjoner
        return selectedRole === 'Administrator';
      
      case 'ADMIN_ORGANISATIONS':
        // Full CRUD på alle organisasjoner - kun Administrator
        return selectedRole === 'Administrator';
      
      default:
        return false;
    }
  };

  return (
    <RoleContext.Provider value={{ selectedRole, setSelectedRole, hasPermission }}>
      {children}
    </RoleContext.Provider>
  );
}

export function useRole() {
  const context = useContext(RoleContext);
  if (context === undefined) {
    throw new Error('useRole must be used within a RoleProvider');
  }
  return context;
}