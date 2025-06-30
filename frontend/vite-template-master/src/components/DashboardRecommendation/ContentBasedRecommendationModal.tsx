import { Modal, Grid, Card, AspectRatio, Skeleton, Menu, Image, HoverCard, Text, Combobox, InputBase, useCombobox, Loader, Input, Group, Container, Center } from '@mantine/core';
import { CardMenu } from '../CardMenu/CardMenu';
import classes from '../DashboardDiscover/NewReleases.module.css';
import { useEffect, useState } from 'react';
import axios from 'axios';
import { IconBrandSpotify, IconDatabase, IconArrowsShuffle2 } from '@tabler/icons-react';
import spotifyLogo from '../../svg/Full_Logo_Black_RGB.svg';



interface Playlist {
  id: number;
  name: string;
  createdAt: string;
  numberOfTracks: number;
}

interface Item {
  emoji: any
  value: string;
  description: string;
}

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

export function ContentRecommendationModal({ isOpen, onClose }: { isOpen: boolean, onClose: () => void }) {

  const [valuePlaylist, setValuePlaylist] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [dataPlaylist, setDataPlaylist] = useState<Playlist[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [contentBased, setContentBased] = useState(null as any);





  useEffect(() => {
    if (valuePlaylist  && dataPlaylist.length > 0) {
      setIsLoading(true);
      axios.get('http://localhost:9191/recommendations/content', {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('jwtToken')}`
        },
        params: {
          playlistId: dataPlaylist.find((item) => item.name === valuePlaylist)?.id,
        }
      })
        .then((response) => {
          if(response.data.tracks.items === undefined) {
          console.log('API response:', response.data.tracks);
          setContentBased(response.data.tracks);
        }
      else{
        console.log('API response2:', response.data.tracks);
        setContentBased(response.data.tracks.items);
      }})
        .catch((error) => {
          console.error('Error fetching content-based recommendations:', error);
        })
        .finally(() => {
          setIsLoading(false);
        });
    }
  }, [valuePlaylist, dataPlaylist]);

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

  function SelectOption({ emoji, value, description }: Item) {
    return (
      <Group>
        <Text fz={9}>{emoji}</Text>
        <div>
          <Text fz="sm" fw={500} lineClamp={1}>
            {value}
          </Text>
        </div>
      </Group>
    );
  }

  const comboboxPlaylist = useCombobox({
    onDropdownClose: () => comboboxPlaylist.resetSelectedOption(),
    onDropdownOpen: () => {
      if (dataPlaylist.length === 0 && !loading) {
        setLoading(true);
        axios.get('http://localhost:9191/playlists', {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('jwtToken')}`
          }
        }
        )
          .then((response) => {
            setDataPlaylist(response.data);
            setLoading(false);
            comboboxPlaylist.resetSelectedOption();
          });
      }
    },
  });

  const comboboxMode = useCombobox({
    onDropdownClose: () => comboboxMode.resetSelectedOption(),
  });

  const optionsPlaylist = dataPlaylist.map((item) => (
    <Combobox.Option value={item.name} key={item.id}>
      <Text size='sm' lineClamp={2}>{item.name}</Text>
    </Combobox.Option>
  ));



  return (
    <Modal opened={isOpen} onClose={onClose} centered size="lg" >
  <Center>
    <Combobox
      store={comboboxPlaylist}
      withinPortal={false}
      transitionProps={{ duration: 200, transition: 'pop' }}
      onOptionSubmit={(val) => {
        setValuePlaylist(val);
        comboboxPlaylist.closeDropdown();
      }}
    >
      <Combobox.Target>
        <InputBase
          w={250}
          size='md'
          component="button"
          type="button"
          pointer
          rightSection={loading ? <Loader size={18} /> : <Combobox.Chevron />}
          onClick={() => comboboxPlaylist.toggleDropdown()}
          rightSectionPointerEvents="none"
        >
          <Text lineClamp={1}>{valuePlaylist || <Input.Placeholder>Pick playlist</Input.Placeholder>}</Text>
        </InputBase>
      </Combobox.Target>

      <Combobox.Dropdown>
        <Combobox.Options>
          {loading ? <Combobox.Empty>Loading....</Combobox.Empty> : optionsPlaylist}
        </Combobox.Options>
      </Combobox.Dropdown>
    </Combobox>
  </Center>

      <Grid justify="flex-start" gutter="xs" ml="xl" mt={20} h={750}>
        {isLoading ? (
          renderSkeletons()
        ) : contentBased && contentBased.length === 0 && valuePlaylist ? (
          <Center style={{ width: '100%', height: '100%' }}>
            <Text size="md">No recommendation found.</Text>
          </Center>
        ) : (
          contentBased?.map((item: any, index: number) => {
            const song = item.track ? item.track : item;
            const albumImage = song.image;
            const artists = song.artists;
            
            return (
              <Grid.Col
                span={4}
                key={index}
                style={{
                  flexBasis: 'calc(33.3333% - 16px)',
                }}
              >
                <img src={spotifyLogo} alt="Vinyl Logo" width={70} />
                <Menu>
                  <Menu.Target>
                    <Card shadow="sm" padding="sm" className={classes.card} withBorder>
                      <AspectRatio ratio={1}>
                        <Image
                          w="105px"
                          src={albumImage}
                          alt={song.name}
                        />
                      </AspectRatio>
          
                      <HoverCard shadow="md" openDelay={500}>
                        <HoverCard.Target>
                          <Text className={classes.title} lineClamp={1}>
                            {song.name}
                          </Text>
                        </HoverCard.Target>
                        <HoverCard.Dropdown>
                          <Text size='sm'>{song.name}</Text>
                        </HoverCard.Dropdown>
                      </HoverCard>
                      <HoverCard shadow="md" openDelay={500}>
                        <HoverCard.Target>
                          <Text className={classes.artist} size="sm" lineClamp={1}>
                            {artists}
                          </Text>
                        </HoverCard.Target>
                        <HoverCard.Dropdown>
                          <Text size='sm'>{artists}</Text>
                        </HoverCard.Dropdown>
                      </HoverCard>
                    </Card>
                  </Menu.Target>
                  <CardMenu
                    track={song}
                    artist={artists}
                  />
                </Menu>
                {song.similarity != null && (
                  <Text size='sm' mt={10} c="gray" ta="center">
                    Similarity: {song.similarity.toFixed(2)}
                  </Text>
                )}
              </Grid.Col>
            );
          })
          
        )}
      </Grid>
    </Modal>
  );
}
