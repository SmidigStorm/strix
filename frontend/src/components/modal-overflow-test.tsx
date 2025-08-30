import { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';

/**
 * Test component to reproduce modal overflow issues
 * This component creates scenarios with very long content to test modal behavior
 */
export function ModalOverflowTest() {
  const [isOpen, setIsOpen] = useState(false);
  
  // Test data with very long strings to trigger overflow
  const longTitle = "Master i Anvendt Datavitenskap og Maskinlæring med Spesialisering i Kunstig Intelligens og Robotikk ved Norges Teknisk-Naturvitenskapelige Universitet Campus Gjøvik";
  const longDescription = "Denne utdanningen er en svært omfattende og detaljert mastergrad som dekker alle aspekter av moderne datavitenskap, inkludert maskinlæring, dyp læring, naturlig språkbehandling, computer vision, robotikk, automatisering, statistisk analyse, big data behandling, cloud computing, distributed systems, cybersikkerhet, blockchain teknologi, Internet of Things (IoT), edge computing, quantum computing, og mye mer. Studenter vil lære avanserte matematiske konsepter, algoritmeutvikling, programmeringsspråk som Python, R, Java, C++, JavaScript, og Scala, samt moderne utviklingsverktøy og metodikker.";
  const longAddress = "Høgskoleringen 5, Teknologi- og vitenskapsbygget, Laboratorium for Avansert Datavitenskap og Maskinlæring, Norges Teknisk-Naturvitenskapelige Universitet, Campus Gjøvik";
  
  const veryLongOptions = [
    "Bachelor i Informatikk med Spesialisering i Programvareutvikling og Systemarkitektur",
    "Master i Cybersikkerhet og Informasjonssikkerhet med Fokus på Penetrasjonstesting",
    "PhD i Kunstig Intelligens og Maskinlæring med Anvendelser innen Helseteknologi",
    "Fagskole i Avansert Webutvikling med React, Node.js og Cloud Computing",
    "Årsstudium i Game Development og Virtual Reality med Unity og Unreal Engine",
    "Videreutdanning i DevOps, Containerisering og Kubernetes for Utviklere og Systemadministratorer"
  ];

  return (
    <div className="p-8 space-y-4">
      <h1 className="text-2xl font-bold">Modal Overflow Tests</h1>
      <p className="text-muted-foreground">
        Test different overflow scenarios in modals to identify and fix issues.
      </p>

      {/* Test 1: Normal modal - should work fine */}
      <Dialog>
        <DialogTrigger asChild>
          <Button variant="outline">Test 1: Normal Content</Button>
        </DialogTrigger>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>Normal Modal</DialogTitle>
            <DialogDescription>
              This modal has normal content that should display correctly.
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div>
              <Label htmlFor="normal-name">Name</Label>
              <Input id="normal-name" placeholder="Enter name" />
            </div>
            <div>
              <Label htmlFor="normal-description">Description</Label>
              <Input id="normal-description" placeholder="Enter description" />
            </div>
          </div>
          <DialogFooter>
            <Button type="button" variant="outline">Cancel</Button>
            <Button type="button">Save</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Test 2: Long title and description */}
      <Dialog>
        <DialogTrigger asChild>
          <Button variant="outline">Test 2: Long Title & Description</Button>
        </DialogTrigger>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>{longTitle}</DialogTitle>
            <DialogDescription>
              {longDescription}
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div>
              <Label htmlFor="long-name">Name</Label>
              <Input id="long-name" placeholder="Enter name" />
            </div>
          </div>
          <DialogFooter>
            <Button type="button" variant="outline">Cancel</Button>
            <Button type="button">Save</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Test 3: Many form fields */}
      <Dialog>
        <DialogTrigger asChild>
          <Button variant="outline">Test 3: Many Form Fields</Button>
        </DialogTrigger>
        <DialogContent className="sm:max-w-[625px]">
          <DialogHeader>
            <DialogTitle>Create New Education Program</DialogTitle>
            <DialogDescription>
              Fill in all the required information for the new education program.
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            {/* Simulate the utdanning form with many fields */}
            <div className="grid grid-cols-2 gap-4">
              <div className="col-span-2">
                <Label htmlFor="program-name">Program Name</Label>
                <Input id="program-name" placeholder="Enter program name" />
              </div>
              <div>
                <Label htmlFor="study-level">Study Level</Label>
                <Select>
                  <SelectTrigger>
                    <SelectValue placeholder="Select level" />
                  </SelectTrigger>
                  <SelectContent>
                    {veryLongOptions.map((option, index) => (
                      <SelectItem key={index} value={option.toLowerCase().replace(/\s+/g, '-')}>
                        {option}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div>
                <Label htmlFor="credits">Credits</Label>
                <Input id="credits" type="number" placeholder="180" />
              </div>
              <div>
                <Label htmlFor="duration">Duration (semesters)</Label>
                <Input id="duration" type="number" placeholder="6" />
              </div>
              <div>
                <Label htmlFor="location">Study Location</Label>
                <Input id="location" defaultValue={longAddress} />
              </div>
              <div>
                <Label htmlFor="language">Teaching Language</Label>
                <Input id="language" placeholder="Norwegian" />
              </div>
              <div>
                <Label htmlFor="start-time">Start Time</Label>
                <Select>
                  <SelectTrigger>
                    <SelectValue placeholder="Select start time" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="fall-2025">Fall 2025</SelectItem>
                    <SelectItem value="spring-2026">Spring 2026</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div>
                <Label htmlFor="study-form">Study Form</Label>
                <Select>
                  <SelectTrigger>
                    <SelectValue placeholder="Select form" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="full-time">Full Time</SelectItem>
                    <SelectItem value="part-time">Part Time</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div>
                <Label htmlFor="organization">Organization</Label>
                <Select>
                  <SelectTrigger>
                    <SelectValue placeholder="Select organization" />
                  </SelectTrigger>
                  <SelectContent>
                    {veryLongOptions.slice(0, 3).map((option, index) => (
                      <SelectItem key={index} value={option.toLowerCase().replace(/\s+/g, '-')}>
                        {option}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div>
                <Label htmlFor="contact-email">Contact Email</Label>
                <Input id="contact-email" type="email" placeholder="contact@university.no" />
              </div>
              <div>
                <Label htmlFor="phone">Phone</Label>
                <Input id="phone" placeholder="+47 123 45 678" />
              </div>
              <div>
                <Label htmlFor="website">Website</Label>
                <Input id="website" placeholder="https://university.no/program" />
              </div>
              <div className="col-span-2">
                <Label htmlFor="description">Program Description</Label>
                <textarea 
                  id="description" 
                  className="flex min-h-[80px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                  placeholder="Describe the program..."
                  defaultValue={longDescription}
                />
              </div>
              <div className="col-span-2">
                <Label htmlFor="requirements">Entry Requirements</Label>
                <textarea 
                  id="requirements" 
                  className="flex min-h-[60px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                  placeholder="List entry requirements..."
                />
              </div>
              <div className="col-span-2">
                <Label htmlFor="career-prospects">Career Prospects</Label>
                <textarea 
                  id="career-prospects" 
                  className="flex min-h-[60px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                  placeholder="Describe career opportunities..."
                />
              </div>
            </div>
          </div>
          <DialogFooter>
            <Button type="button" variant="outline">Cancel</Button>
            <Button type="button">Create Program</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Test 4: Mobile responsive test */}
      <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogTrigger asChild>
          <Button variant="outline">Test 4: Mobile Responsiveness</Button>
        </DialogTrigger>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>Mobile Responsive Test</DialogTitle>
            <DialogDescription>
              This modal should work well on mobile devices. Try resizing your browser window to see how it behaves.
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div>
              <Label htmlFor="mobile-name">Program Name</Label>
              <Input id="mobile-name" defaultValue={longTitle} />
            </div>
            <div>
              <Label htmlFor="mobile-location">Location</Label>
              <Input id="mobile-location" defaultValue={longAddress} />
            </div>
            <div>
              <Label htmlFor="mobile-description">Description</Label>
              <textarea 
                id="mobile-description" 
                className="flex min-h-[100px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                defaultValue={longDescription}
              />
            </div>
          </div>
          <DialogFooter className="flex-col sm:flex-row gap-2">
            <Button type="button" variant="outline" onClick={() => setIsOpen(false)}>Cancel</Button>
            <Button type="button" onClick={() => setIsOpen(false)}>Save</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}