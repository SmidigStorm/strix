import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useRole, type Role } from '@/contexts/RoleContext';
import { useAuth, useTestUsers } from '@/contexts/AuthContext';
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from '@/components/ui/sidebar';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Separator } from '@/components/ui/separator';
import {
  BarChart3,
  ChevronDown,
  University,
  GraduationCap as StudentIcon,
  Code,
  Building2,
  UserCheck,
  Users,
} from 'lucide-react';

const getRoleIcon = (role: Role) => {
  switch (role) {
    case 'Administrator': return University;
    case 'Opptaksleder': return UserCheck;
    case 'Søknadsbehandler': return Users;
    case 'Søker': return StudentIcon;
    default: return StudentIcon;
  }
};

export function AppSidebar() {
  const { selectedRole, hasPermission } = useRole();
  const { user, login, logout, isAuthenticated } = useAuth();
  const testUsers = useTestUsers();
  const location = useLocation();
  const RoleIcon = getRoleIcon(selectedRole);

  return (
    <Sidebar>
      <SidebarHeader>
        <SidebarMenu>
          <SidebarMenuItem>
            <div className="flex items-center gap-3 px-2 py-1">
              <div className="w-10 h-10 rounded-full overflow-hidden bg-white flex-shrink-0">
                <img 
                  src="/owl-logo.png" 
                  alt="Strix Logo" 
                  className="w-full h-full object-cover"
                />
              </div>
              <div className="min-w-0">
                <h1 className="text-sm font-medium truncate">Strix</h1>
                <p className="text-xs text-muted-foreground truncate">Opptak</p>
              </div>
            </div>
          </SidebarMenuItem>
          <SidebarMenuItem>
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <SidebarMenuButton className="w-full justify-between h-12">
                  <div className="flex items-center gap-3">
                    <RoleIcon className="h-5 w-5" />
                    <div className="text-left">
                      {isAuthenticated ? (
                        <>
                          <div className="text-sm font-semibold">{user?.navn}</div>
                          <div className="text-xs text-muted-foreground">{selectedRole}</div>
                        </>
                      ) : (
                        <span className="text-base font-semibold">Logg inn</span>
                      )}
                    </div>
                  </div>
                  <ChevronDown className="h-4 w-4" />
                </SidebarMenuButton>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="start" className="w-64">
                {isAuthenticated ? (
                  <>
                    <div className="px-2 py-1.5 text-sm text-muted-foreground">
                      Innlogget som: {user?.email}
                    </div>
                    <DropdownMenuItem
                      onClick={logout}
                      className="flex items-center gap-2 text-red-600"
                    >
                      Logg ut
                    </DropdownMenuItem>
                  </>
                ) : (
                  <>
                    <div className="px-2 py-1.5 text-sm font-semibold">Test-brukere (passord: test123)</div>
                    {testUsers.map((testUser) => (
                      <DropdownMenuItem
                        key={testUser.email}
                        onClick={() => login(testUser.email)}
                        className="flex items-center gap-2"
                      >
                        {React.createElement(getRoleIcon(testUser.rolle as Role), { className: "h-4 w-4" })}
                        <div className="flex-1">
                          <div className="text-sm">{testUser.navn}</div>
                          <div className="text-xs text-muted-foreground">{testUser.rolle}</div>
                        </div>
                      </DropdownMenuItem>
                    ))}
                  </>
                )}
              </DropdownMenuContent>
            </DropdownMenu>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarHeader>

      <SidebarContent>
        <Separator />

        {/* Hovednavigasjon */}
        <SidebarGroup>
          <SidebarGroupContent className="mt-4">
            <SidebarMenu>
              <SidebarMenuItem>
                <SidebarMenuButton asChild isActive={location.pathname === '/'}>
                  <Link to="/" className="flex items-center gap-3">
                    <BarChart3 className="h-4 w-4" />
                    <span>Dashboard</span>
                  </Link>
                </SidebarMenuButton>
              </SidebarMenuItem>
              {hasPermission('VIEW_ORGANISATIONS') && (
                <SidebarMenuItem>
                  <SidebarMenuButton asChild isActive={location.pathname === '/organisasjoner'}>
                    <Link to="/organisasjoner" className="flex items-center gap-3">
                      <Building2 className="h-4 w-4" />
                      <span>Organisasjoner</span>
                    </Link>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              )}
              <SidebarMenuItem>
                <SidebarMenuButton asChild>
                  <a href="/graphiql" target="_blank" className="flex items-center gap-3">
                    <Code className="h-4 w-4" />
                    <span>GraphiQL</span>
                  </a>
                </SidebarMenuButton>
              </SidebarMenuItem>
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>

        {/* Info om fremtidige funksjoner */}
        {selectedRole === 'Søker' && (
          <SidebarGroup>
            <SidebarGroupContent className="mt-4">
              <div className="flex flex-col items-center justify-center py-8 text-center">
                <StudentIcon className="h-8 w-8 text-muted-foreground mb-2" />
                <p className="text-sm text-muted-foreground">Søkerfunksjoner kommer snart</p>
              </div>
            </SidebarGroupContent>
          </SidebarGroup>
        )}
        {selectedRole === 'Søknadsbehandler' && (
          <SidebarGroup>
            <SidebarGroupContent className="mt-4">
              <div className="flex flex-col items-center justify-center py-8 text-center">
                <Users className="h-8 w-8 text-muted-foreground mb-2" />
                <p className="text-sm text-muted-foreground">Behandlerfunksjoner kommer snart</p>
              </div>
            </SidebarGroupContent>
          </SidebarGroup>
        )}
      </SidebarContent>
    </Sidebar>
  );
}