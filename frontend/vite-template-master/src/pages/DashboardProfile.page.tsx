import { ProfileCard } from "@/components/DashboardProfile/ProfileCard";
import { ProfileData } from "@/components/DashboardProfile/ProfileData";
import { Center, Stack } from "@mantine/core";


export function DashboardProfile() {
    return (
        <>
        <Center>
            <ProfileCard />
        </Center>
        <ProfileData />

        </>
    )
}