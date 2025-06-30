import { HeroRecommendation } from "@/components/DashboardRecommendation/HeroRecommendation";
import { RecommendationButtons } from "@/components/DashboardRecommendation/RecommendationButtons";
import { ManualRecommendation } from "@/components/DashboardRecommendation/RecommendationManual";
import { Stack, Center } from "@mantine/core";


export function DashboardRecommendation() {
    return(

        <>
        <Center>
            <Stack align="center">
                <HeroRecommendation />
                <RecommendationButtons />
                <ManualRecommendation />
            </Stack>
        </Center>
        </>
    )

}