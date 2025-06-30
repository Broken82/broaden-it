import { Container, Text, Card, Group, Image, Menu, AspectRatio, HoverCard } from '@mantine/core';
import classes from './Top10.module.css';
import classesCard from '../DashboardDiscover/NewReleases.module.css';
import { CardMenu } from '../CardMenu/CardMenu';
import { useEffect, useState } from 'react';
import axios from 'axios';

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

export function Top10() {

  const [songs, setSongs] = useState<Track[]>([]);

  useEffect(() => {
    axios.get<Response>('http://localhost:9191/trending')
      .then((response) => {
        setSongs(response.data.tracks);  
      })
      .catch((error) => {
        console.log('Error:', error);
      })
      .finally(() => {
        console.log('Trending now:', songs);
      });
  }, []);


  return (
    <Container className={classes.container}>
      <Text className={classes.title}>
        Check out the top 10 most popular songs
      </Text>

      <Group className={classesCard.cards} >
        {songs.slice(0,10).map((song, index) => (
          <Menu>
          <Menu.Target>
            <Card shadow="sm" padding="sm" className={classesCard.card} withBorder>
              <AspectRatio ratio={1}>
                <Image
                  w="105px"
                  src={
                    song.image
                  }
                  radius="lg"
                  alt={song.name}
                />
              </AspectRatio>
            
              <HoverCard shadow="md" openDelay={500}>
                <HoverCard.Target>
                  <Text className={classesCard.title} lineClamp={1}>{song.name}</Text>
                </HoverCard.Target>
                <HoverCard.Dropdown>
                  <Text size='sm'>{song.name}</Text>
                </HoverCard.Dropdown>
              </HoverCard>
              <HoverCard shadow="md" openDelay={500}>
                <HoverCard.Target>
                  <Text className={classesCard.artist} size="sm" lineClamp={1}>{song.artists}</Text>
                </HoverCard.Target>
                <HoverCard.Dropdown>
                  <Text size='sm'>{song.artists}</Text>
                </HoverCard.Dropdown>
            </HoverCard>
            </Card>
          </Menu.Target>
          <CardMenu

            track={song}
            artist={song.artists}
          />
        </Menu>
        ))}
      </Group>
    </Container>
  );
}
