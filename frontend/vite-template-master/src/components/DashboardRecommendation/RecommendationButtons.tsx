import { Button, Group, Stack, Text, Container, Flex, Box } from "@mantine/core";
import { useDisclosure } from '@mantine/hooks';
import { ContentRecommendationModal} from "./ContentBasedRecommendationModal";
import axios from "axios";
import { useEffect, useState } from "react";
import { CollabRecommendationModal } from "./CollabFilteringRecommendationModal";


interface Track {
  image: string
  artists: string
  href: string;
  name: string;
  preview_url: string;
}

interface Response {
  tracks: Track[];
}


export function RecommendationButtons(){
  const [opened, { open, close }] = useDisclosure(false);
  const [openedCollab, { open: openCollab, close: closeCollab }] = useDisclosure(false)





    return(
<>
        <Group mt="lg" gap="xl">
        <div>
        <Button onClick={open}  variant="gradient" gradient={{ from: '#ed6ea0', to: '#ec8c69' }} size="lg" >
          Content-Based Filtering
        </Button>
        <Text c="dimmed" ta="center">Based on track properites</Text>
        </div>
        <div>
        <Button onClick={openCollab} variant="gradient" gradient={{ from: '#6a11cb', to: '#2575fc' }} size="lg" >
          Collaborative Filtering
        </Button>
        <Text c="dimmed" ta="center">Based on similarities between users</Text>
        </div>
      </Group>



      <ContentRecommendationModal isOpen={opened} onClose={close}/>
      <CollabRecommendationModal isOpen={openedCollab} onClose={closeCollab}/>

      </>
    )
}