import { Hero } from "@/components/DashboardHome/Hero";
import { Top10 } from "@/components/DashboardHome/Top10";
import { Stack } from "@mantine/core";



export function DashboardHome() {
    return (
<>
          <Stack>
        <Hero/>
        <Top10/>
        </Stack>
        </>
    );
  }