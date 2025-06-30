import {
  Modal,
  Grid,
  Card,
  Text,
  AspectRatio,
  Image,
  Menu,
  Skeleton,
  HoverCard,
} from '@mantine/core';
import classes from '../DashboardDiscover/NewReleases.module.css';
import { CardMenu } from '../CardMenu/CardMenu';
import spotifyLogo from '../../svg/Full_Logo_Black_RGB.svg';

interface Track {
  image: string
  artists: string
  href: string;
  name: string;
  preview_url: string;
  similarity: number;
}

interface Response {
  tracks: Track[];
}


export function ManualModal({ isOpen, onClose, tracks, isLoading }: { isOpen: boolean, onClose: () => void, tracks: any, isLoading: boolean }) {

    function renderSkeletons() {
        const skeletons = [];
        for (let i = 0; i < 12; i++) {
          skeletons.push(
            <Grid.Col
              span={4}
              key={i}
              style={{
                flexBasis: 'calc(33.3333% - 16px)',
              }}
            >
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
      };

    return (
        <Modal opened={isOpen} onClose={onClose} centered size="lg">
          <Grid justify="flex-start" gutter="xs" ml="xl">
            {isLoading
              ? renderSkeletons() 
              : tracks.map((song: Track, index: number) => (
                  <Grid.Col
                    span={4}
                    key={index}
                    style={{
                      flexBasis: 'calc(33.3333% - 16px)',
                    }}
                  >
                    <img src={spotifyLogo} alt="Vinyl Logo" width={70}  />
                    <Menu>
                      <Menu.Target>
                        <Card shadow="sm" padding="sm" className={classes.card} withBorder>
                          <AspectRatio ratio={1}>
                            <Image
                              w="105px"
                              src={
                                song.image
                              }
                              radius="4px"
                              alt={song.name}
                            />
                          </AspectRatio>
                        
                          <HoverCard shadow="md" openDelay={500}>
                            <HoverCard.Target>
                              <Text className={classes.title} lineClamp={1}>{song.name}</Text>
                            </HoverCard.Target>
                            <HoverCard.Dropdown>
                              <Text size='sm'>{song.name}</Text>
                            </HoverCard.Dropdown>
                          </HoverCard>
                          <HoverCard shadow="md" openDelay={500}>
                            <HoverCard.Target>
                              <Text className={classes.artist} size="sm" lineClamp={1}>{song.artists}</Text>
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
                    <Text size='sm' mt={10} c="gray" ta="center">
                    Similarity: {song.similarity.toFixed(2)}
                  </Text>
                  </Grid.Col>
                ))}
          </Grid>
        </Modal>
      );

}
