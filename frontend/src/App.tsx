import { SidebarProvider, SidebarTrigger } from '@/components/ui/sidebar';
import { AppSidebar } from '@/components/app-sidebar';
import Dashboard from '@/components/dashboard';
import { Button } from '@/components/ui/button';

function App() {
  return (
    <SidebarProvider>
      <div className="flex min-h-screen w-full bg-background">
        <AppSidebar />

        <main className="flex-1 flex flex-col min-w-0">
          {/* Header */}
          <header className="border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
            <div className="flex h-14 items-center px-4">
              <SidebarTrigger />
              <div className="ml-auto flex items-center space-x-4">
                <Button variant="outline" size="sm">
                  Logg inn
                </Button>
              </div>
            </div>
          </header>

          {/* Main content */}
          <div className="flex-1 flex justify-center p-6">
            <div className="w-full max-w-6xl mx-auto">
              <Dashboard />
            </div>
          </div>
        </main>
      </div>
    </SidebarProvider>
  );
}

export default App
