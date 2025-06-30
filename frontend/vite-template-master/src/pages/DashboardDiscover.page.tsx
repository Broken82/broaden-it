import GenresGrid from "@/components/DashboardDiscover/Genres";
import { HeroDiscover } from "@/components/DashboardDiscover/HeroDiscover";
import { NewReleasesCarousel } from "@/components/DashboardDiscover/NewReleases";
import { TrendingNow } from "@/components/DashboardDiscover/Trending";
import { Stack, Flex, Group, Grid } from "@mantine/core";


export function DashboardDiscover() {
    return (
        <>
        <Stack>
            <HeroDiscover />
            <NewReleasesCarousel />
            </Stack>
            
            <Grid grow justify="flex-start">
                <Grid.Col span={2}><TrendingNow  /></Grid.Col>
                <Grid.Col span={2}><GenresGrid  /></Grid.Col>
                </Grid>
            
        </>
    );
}
