import { Center, Divider, Flex, Grid, Group, Paper, Rating, RingProgress, Stack, Table, Text } from "@mantine/core";
import axios from "axios";
import { useEffect, useState } from "react";







export function ProfileData(){

  const [averageRating, setAverageRating] = useState<number>(0);
  const [numberOfTracks, setNumberOfTracks] = useState<number>(0);
  const [top5Tracks, setTop5Tracks] = useState<any[]>([]);



  const rows = top5Tracks.map((song, index) => (
    <Table.Tr key={song.title}>
      <Table.Td>{index+1}</Table.Td>
      <Table.Td>{song.title}</Table.Td>
      <Table.Td>{song.artist}</Table.Td>
    </Table.Tr>
  ));

  useEffect(() => {
    axios.get('http://localhost:9191/user/profile/stats', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('jwtToken')}`
      }
    })
    .then((response) => {
      setAverageRating(response.data.averageRating);
      setNumberOfTracks(response.data.playlistCount);
      setTop5Tracks(response.data.topTracks);
      console.log(response.data);
    })
    .catch((error) => {
      console.error('Error fetching profile stats:', error);
    }
    )

  }, []);


    return (

        <Group grow mt={30}>
        <Paper shadow="xl" p="xl" radius="md" h={425} mt={30} style={{ backgroundColor: '#D6ECFB' }}>
        <Grid align="stretch">
      <Grid.Col span={4}>
      <Paper p="lg" style={{ backgroundColor: '#EDF6FD' }} h={350}>
            <Text ta="left" size="14px" mt={10} fw={700} >AVERAGE RATING</Text>
            <Divider mt={5} />
            <Stack justify="center" align="center">
            <RingProgress
            label={
                <Center>
                <Stack>
                <Rating value={averageRating} fractions={2} size="xl" readOnly />
                <Text size="30px"  ta="center">{averageRating.toFixed(2)}</Text>
                </Stack>
                </Center>
            }

      size={250}
      thickness={8}
      sections={[
        { value: 100, color: '#B0BEC5' },
      ]}
    />

                
    </Stack>
        </Paper>

      </Grid.Col>
      <Grid.Col span={4}>
      <Paper p="lg" style={{ backgroundColor: '#EDF6FD' }} h={350}>
            <Text ta="left" size="14px" mt={10} fw={700} >SUM OF TRACKS</Text>
            <Divider mt={5} />
      <Stack justify="center" align="center">
            <RingProgress
            label={
                <Center>
                <Stack>
                <Text size="30px"  ta="center">{numberOfTracks}</Text>
                </Stack>
                </Center>
            }

      size={250}
      thickness={8}
      sections={[
        { value: 100, color: '#B0BEC5' },
      ]}
    />
    </Stack>
      </Paper>
      </Grid.Col>
      <Grid.Col span={4}>
      <Paper p="lg" style={{ backgroundColor: '#EDF6FD' }} h={350}>
            <Text ta="left" size="14px" fw={700} mt={10} >TOP 5 TRACKS</Text>
            <Divider mt={5} />
      <Stack justify="center" align="center">
      <Table stickyHeader stickyHeaderOffset={60} mt={30}>
      <Table.Thead bg="#EDF6FD">
        <Table.Tr>
          <Table.Th>#</Table.Th>
          <Table.Th>Title</Table.Th>
          <Table.Th>Artist</Table.Th>
        </Table.Tr>
      </Table.Thead>
      <Table.Tbody>{rows}</Table.Tbody>
    </Table>
    </Stack>
      </Paper>
      </Grid.Col>
    </Grid>

        </Paper>
        </Group>

    )
}