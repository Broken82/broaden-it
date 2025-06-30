import { Navbar } from "@/components/Navbar/Navbar";
import { Hero } from "@/components/DashboardHome/Hero";
import { AppShell, Group } from "@mantine/core";
import { Outlet } from "react-router-dom";
import { Searchbar } from "@/components/SearchBar/Searchbar";
import { AudioProvider } from "@/contexts/AudioProvider";

export function DashboardPage() {
  return (
    <>
    <AppShell
    navbar={{
      width: 300,
      breakpoint: 'sm',
    }}
    padding="md"
    >


        <AppShell.Navbar>
        <Navbar />
        </AppShell.Navbar>
        <AppShell.Main bg="#ebf5ff">
        <AudioProvider>
        <Searchbar />
        <Outlet />
        </AudioProvider>
        </AppShell.Main>

      </AppShell>
    </>
  );
}
