import React, { useEffect, useState } from 'react';
import { Center, Paper, Grid, Card, AspectRatio, Image, Text, Container, Stack, Menu, Skeleton, HoverCard } from "@mantine/core";
import axios from 'axios';
import classes from './NewReleases.module.css';
import headers from './Headers.module.css';
import { CardMenu } from '../CardMenu/CardMenu';
import spotifyLogo from '../../svg/Full_Logo_Black_RGB.svg';


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


const TrendingCard = React.forwardRef<HTMLDivElement, { name: string; artist: string; image: string, track: Track }>(
  ({ name, artist, image, track }, ref) => {



    return (
      <>
      <img src={spotifyLogo} style={{marginLeft: '30px'}} alt="Vinyl Logo" width={70}  />
      <Menu withArrow>
        <Menu.Target>
          <Card ref={ref} ml={30} shadow='sm' padding="sm" className={classes.card} withBorder>
            <AspectRatio ratio={1}>
              <Image w="105px" src={image} radius="4px" alt={name} />
            </AspectRatio>
            <HoverCard shadow="md" openDelay={500}>
              <HoverCard.Target>
            <Text className={classes.title} lineClamp={1}>{name}</Text>
            </HoverCard.Target>
            <HoverCard.Dropdown>
              <Text size='sm'>{name}</Text>
            </HoverCard.Dropdown>
            </HoverCard>
            <HoverCard shadow="md" openDelay={500}>
            <HoverCard.Target>
            <Text className={classes.artist} size="sm" lineClamp={1}>{artist}</Text>
            </HoverCard.Target>
            <HoverCard.Dropdown>
              <Text size='sm'>{artist}</Text>
            </HoverCard.Dropdown>
            </HoverCard>
          </Card>
        </Menu.Target>
        <CardMenu track={track} artist={artist}  />
      </Menu>
      </>
    );
  }
);


export function TrendingNow() {
  const [isLoading, setIsLoading] = useState(false);
  const [trendingNow, setTrendingNow] = useState<Track[]>([]);  

  useEffect(() => {
    setIsLoading(true);
    axios.get<Response>('http://localhost:9191/trending')
      .then((response) => {
        setTrendingNow(response.data.tracks);  
      })
      .catch((error) => {
        console.log('Error:', error);
      })
      .finally(() => {
        setIsLoading(false);
        console.log('Trending now:', trendingNow);
      });
  }, []);


  

  function renderSkeletons() {
    const skeletons = [];
    for (let i = 0; i < 12; i++) {
      skeletons.push(
        <Grid.Col span={4} key={i}>
          <Card shadow="sm" padding="sm" className={classes.card} withBorder>
            <AspectRatio ratio={1}>
              <Skeleton height={105} radius="4px" />
            </AspectRatio>
            <Skeleton height={8} mt="sm" width="80%" />
            <Skeleton height={8} mt="xs" width="60%" />
          </Card>
        </Grid.Col>
      );
    }
    return skeletons;
  }
  

  return ( 
    <>
      <Stack>
        <Text
          className={headers.paperheader}
          pl={30}
          mb={0}
          variant="gradient"
          gradient={{ from: '#20002c', to: '#cbb4d4', deg: 360 }}
        >
          Trending Now
        </Text>
  
        <Center>
          <Paper shadow="xs" radius="xl" p="md" m="md" mt={0} withBorder>
            <Grid justify="flex-start" gutter="md">
              {isLoading
                ? renderSkeletons()
                : trendingNow.map((song, index) => (
                    <Grid.Col span={4} key={index}>
                      <TrendingCard
                        name={song.name}
                        artist={song.artists}
                        image={song.image || 'https://via.placeholder.com/150'}
                        track={song}
                      />
                    </Grid.Col>
                  )).slice(0, 21)}
            </Grid>
          </Paper>
        </Center>
      </Stack>
    </>
  );
}
