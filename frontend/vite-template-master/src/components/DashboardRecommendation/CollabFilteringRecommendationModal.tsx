import { Modal, Grid, Card, AspectRatio, Skeleton, Menu, Image, HoverCard, Text, Combobox, InputBase, useCombobox, Loader, Input, Group, Container, Center, Flex, Alert } from '@mantine/core';
import { CardMenu } from '../CardMenu/CardMenu';
import classes from '../DashboardDiscover/NewReleases.module.css';
import { useEffect, useState } from 'react';
import axios from 'axios';
import { IconInfoCircle } from '@tabler/icons-react';
import spotifyLogo from '../../svg/Full_Logo_Black_RGB.svg';



  
  interface Playlist {
    id: number;
    name: string;
    createdAt: string;
    numberOfTracks: number;
  }
  
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

export function CollabRecommendationModal({ isOpen, onClose}: { isOpen: boolean, onClose: () => void}) {

    const [isLoading, setIsLoading] = useState(false); 
    const [loading, setLoading] = useState(false); 
    const [collabFiltering, setCollabFiltering] = useState<Track[] | null>(null);
    const [dataPlaylist, setDataPlaylist] = useState<Playlist[]>([]);
    const [valuePlaylist, setValuePlaylist] = useState<string | null>(null);

    useEffect(() => {
      if (valuePlaylist && dataPlaylist.length > 0) {
        setIsLoading(true);
        axios.get<Response>('http://localhost:9191/recommendations/collab', {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('jwtToken')}`,
          },
          params: {
            playlistId: dataPlaylist.find((item) => item.name === valuePlaylist)?.id,
          },
        })
          .then((response) => {
            console.log('API response:', response.data.tracks);
            setCollabFiltering(response.data.tracks);
          })
          .catch((error) => {
            console.error('Error fetching content-based recommendations:', error);
          })
          .finally(() => {
            setTimeout(() => {
              setIsLoading(false); 
            }, 500); 
          });
      }
    }, [valuePlaylist, dataPlaylist]);
    


  

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

  <Grid 
    justify="flex-star" 
    align="center" 
    style={{ height: 'calc(100vh - 200px)', display: 'flex' }} 
    ml="xl" 
    mt={20}
  >
    {isLoading ? (
      renderSkeletons()
    ) : collabFiltering && collabFiltering.length === 0 && valuePlaylist ? (
      <Center w={800}>
        <Alert variant="light" color="red" title="Unable to fetch recommendations" icon={<IconInfoCircle/>}>
      Be sure to add and rate more tracks to allow for precise recommendations.
    </Alert>
      </Center>
    ) : (
      collabFiltering?.map((song: Track, index: number) => (
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
              onRemove={() => {}}
              track={song}
              artist={song.artists}
            />
          </Menu>
          <Text size='sm' mt={10} c="gray" ta="center">Predicted rating: {song.similarity.toFixed(2)}</Text>

        </Grid.Col>
      ))
    )}
  </Grid>
</Modal>

  
      );
  }
  