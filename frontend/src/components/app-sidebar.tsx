import { useState } from 'react';
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
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
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from '@/components/ui/collapsible';
import { Separator } from '@/components/ui/separator';
import {
  Building,
  GraduationCap,
  BarChart3,
  ChevronDown,
  ChevronRight,
  University,
  GraduationCap as StudentIcon,
  ClipboardList,
} from 'lucide-react';

type Role = 'Søker' | 'Administrator';

interface MenuGroup {
  title: string;
  icon: React.ComponentType<{ className?: string }>;
  items: Array<{
    title: string;
    icon: React.ComponentType<{ className?: string }>;
    href: string;
  }>;
}

const adminMenuGroups: MenuGroup[] = [
  {
    title: 'Opptak',
    icon: ClipboardList,
    items: [
      {
        title: 'Oversikt',
        icon: BarChart3,
        href: '/opptak',
      },
    ],
  },
  {
    title: 'Organisasjon',
    icon: Building,
    items: [
      {
        title: 'Oversikt',
        icon: BarChart3,
        href: '/organisasjon',
      },
    ],
  },
  {
    title: 'Utdanning',
    icon: GraduationCap,
    items: [
      {
        title: 'Oversikt',
        icon: BarChart3,
        href: '/utdanning',
      },
    ],
  },
];

const søkerMenuGroups: MenuGroup[] = [
  // Kommer senere
];

export function AppSidebar() {
  const [selectedRole, setSelectedRole] = useState<Role>('Administrator');
  const [openGroups, setOpenGroups] = useState<Set<string>>(
    new Set(['Opptak', 'Organisasjon', 'Utdanning'])
  );

  const currentMenuGroups = selectedRole === 'Administrator' ? adminMenuGroups : søkerMenuGroups;
  const RoleIcon = selectedRole === 'Administrator' ? University : StudentIcon;

  const toggleGroup = (groupTitle: string) => {
    const newOpenGroups = new Set(openGroups);
    if (openGroups.has(groupTitle)) {
      newOpenGroups.delete(groupTitle);
    } else {
      newOpenGroups.add(groupTitle);
    }
    setOpenGroups(newOpenGroups);
  };

  return (
    <Sidebar>
      <SidebarHeader>
        <SidebarMenu>
          <SidebarMenuItem>
            <div className="flex items-center gap-2 px-2 py-1">
              <div className="w-8 h-8 rounded-full overflow-hidden bg-white flex-shrink-0">
                <img 
                  src="/owl-logo.png" 
                  alt="Strix Logo" 
                  className="w-full h-full object-cover"
                />
              </div>
              <div className="min-w-0">
                <h1 className="text-xs font-medium truncate">Strix</h1>
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

        {/* Dashboard som egen seksjon */}
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
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>

        {/* Kollapsible grupper */}
        {selectedRole === 'Administrator' ? (
          currentMenuGroups.map((group) => (
            <Collapsible
              key={group.title}
              open={openGroups.has(group.title)}
              onOpenChange={() => toggleGroup(group.title)}
            >
              <SidebarGroup>
                <SidebarGroupLabel asChild>
                  <CollapsibleTrigger className="group/collapsible flex w-full items-center gap-2 rounded-md p-2 text-sm font-medium hover:bg-sidebar-accent hover:text-sidebar-accent-foreground">
                    <group.icon className="h-4 w-4" />
                    <span className="flex-1 text-left">{group.title}</span>
                    <ChevronRight className="h-4 w-4 transition-transform group-data-[state=open]/collapsible:rotate-90" />
                  </CollapsibleTrigger>
                </SidebarGroupLabel>
                <CollapsibleContent>
                  <SidebarGroupContent>
                    <SidebarMenu>
                      {group.items.map((item) => (
                        <SidebarMenuItem key={item.href}>
                          <SidebarMenuButton asChild>
                            <a href={item.href} className="flex items-center gap-3 pl-6">
                              <item.icon className="h-4 w-4" />
                              <span>{item.title}</span>
                            </a>
                          </SidebarMenuButton>
                        </SidebarMenuItem>
                      ))}
                    </SidebarMenu>
                  </SidebarGroupContent>
                </CollapsibleContent>
              </SidebarGroup>
            </Collapsible>
          ))
        ) : (
          <SidebarGroup>
            <SidebarGroupContent className="mt-4">
              <div className="flex flex-col items-center justify-center py-8 text-center">
                <StudentIcon className="h-8 w-8 text-muted-foreground mb-2" />
                <p className="text-sm text-muted-foreground">Søkermeny kommer snart</p>
              </div>
            </SidebarGroupContent>
          </SidebarGroup>
        )}
      </SidebarContent>
    </Sidebar>
  );
}