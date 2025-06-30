import { Container, Card, Image, Text, Group, AspectRatio, Paper, Menu, Skeleton, HoverCard } from '@mantine/core';
import { Carousel } from '@mantine/carousel';
import classes from './NewReleases.module.css';
import headers from './Headers.module.css';
import { CardMenu } from '../CardMenu/CardMenu';
import React, { useEffect, useState } from 'react';
import axios from 'axios';
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

const NewReleases = ({ track, name, artist, image }: { track: Track; name: string; artist: string; image: string }) => {
  return (
    <>
      <img src={spotifyLogo} alt="Spotify Logo" width={70} />
      <Menu withArrow>
        <Menu.Target>
          <Card shadow="sm" padding="sm" className={classes.card} withBorder>
            <div className={classes.imageContainer}>
              <AspectRatio ratio={1}>
                <Image
                  w="105px"
                  src={image}
                  radius="4px"
                  alt={name}
                />
              </AspectRatio>
            </div>
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
        <CardMenu track={track} artist={artist} />
      </Menu>
    </>
  );
};
export function NewReleasesCarousel() {
  const [isLoading, setIsLoading] = useState(false);
  const [newReleases, setNewReleases] = useState<Track[]>([]);

  useEffect(() => {
    setIsLoading(true);
    axios
      .get<Response>('http://localhost:9191/new')
      .then((response) => {
        setNewReleases(response.data.tracks);
        console.log("new" + response.data.tracks);
      })
      .catch((error) => {
        console.error('Error fetching new releases:', error);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }, []);

  function renderSkeletons() {
    const skeletons = [];
    for (let i = 0; i < 12; i++) {
      skeletons.push(
        <Carousel.Slide key={i} pb="5px" pt="5px" pl="5px">
          <Card shadow="sm" padding="sm" className={classes.card} withBorder>
            <AspectRatio ratio={1}>
              <Skeleton height={105} radius="4px" />
            </AspectRatio>
            <Skeleton height={8} mt="sm" width="80%" />
            <Skeleton height={8} mt="xs" width="60%" />
          </Card>
        </Carousel.Slide>
      );
    }
    return skeletons;
  }

  const slides = newReleases.map((song, index) => (
    <Carousel.Slide key={index} pb="5px" pt="5px" pl="5px">
      <NewReleases
        track={song}
        name={song.name}
        artist={song.artists}
        image={song.image}
      />
    </Carousel.Slide>
  ));

  return (
    <>
      <Text
        className={headers.paperheader}
        pl={30}
        mb={0}
        variant="gradient"
        gradient={{ from: '#6D6027', to: '#D3CBB8', deg: 360 }}
      >
        New Releases
      </Text>
      <Paper shadow="xs" radius="xl" p="md" m="md" mt={0} withBorder>
        <Carousel slideSize="auto" slideGap="85px" align="start" slidesToScroll={1}>
          {isLoading ? renderSkeletons() : slides}
        </Carousel>
      </Paper>
    </>
  );
}
