import { createContext, useContext, useState, ReactNode } from 'react';

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
    console.log(`Checking permission "${permission}" for role "${selectedRole}"`);
    switch (permission) {
      case 'VIEW_ORGANISATIONS':
        // Alle roller kan se organisasjoner (i ulike nivåer)
        return true;
      
      case 'CREATE_ORGANISATION':
        // Kun Administrator kan opprette organisasjoner
        const canCreate = selectedRole === 'Administrator';
        console.log(`  CREATE_ORGANISATION result: ${canCreate}`);
        return canCreate;
      
      case 'EDIT_ORGANISATION':
        // Kun Administrator kan redigere alle organisasjoner
        // Opptaksleder kan kun se og redigere egen organisasjonsinformasjon (ikke implementert ennå)
        const canEdit = selectedRole === 'Administrator';
        console.log(`  EDIT_ORGANISATION result: ${canEdit}`);
        return canEdit;
      
      case 'DELETE_ORGANISATION':
        // Kun Administrator kan deaktivere/slette organisasjoner
        const canDelete = selectedRole === 'Administrator';
        console.log(`  DELETE_ORGANISATION result: ${canDelete}`);
        return canDelete;
      
      case 'ADMIN_ORGANISATIONS':
        // Full CRUD på alle organisasjoner - kun Administrator
        const canAdmin = selectedRole === 'Administrator';
        console.log(`  ADMIN_ORGANISATIONS result: ${canAdmin}`);
        return canAdmin;
      
      default:
        console.log(`  Unknown permission, returning false`);
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