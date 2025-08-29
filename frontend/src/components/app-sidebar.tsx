import { useState } from 'react';
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
} from 'lucide-react';

type Role = 'Søker' | 'Administrator';

export function AppSidebar() {
  const [selectedRole, setSelectedRole] = useState<Role>('Administrator');
  const RoleIcon = selectedRole === 'Administrator' ? University : StudentIcon;

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
                    <span className="text-base font-semibold">{selectedRole}</span>
                  </div>
                  <ChevronDown className="h-4 w-4" />
                </SidebarMenuButton>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="start" className="w-56">
                <DropdownMenuItem
                  onClick={() => setSelectedRole('Søker')}
                  className="flex items-center gap-2"
                >
                  <StudentIcon className="h-4 w-4" />
                  Søker
                </DropdownMenuItem>
                <DropdownMenuItem
                  onClick={() => setSelectedRole('Administrator')}
                  className="flex items-center gap-2"
                >
                  <University className="h-4 w-4" />
                  Administrator
                </DropdownMenuItem>
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
                <SidebarMenuButton asChild>
                  <a href="#dashboard" className="flex items-center gap-3">
                    <BarChart3 className="h-4 w-4" />
                    <span>Dashboard</span>
                  </a>
                </SidebarMenuButton>
              </SidebarMenuItem>
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
      </SidebarContent>
    </Sidebar>
  );
}