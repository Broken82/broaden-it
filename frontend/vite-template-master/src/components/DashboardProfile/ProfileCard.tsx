import { AspectRatio, Paper, Image, Center, Text, Stack, Container, Group } from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";
import { SearchModal } from "../SearchBar/SearchbarModal";
import { useEffect, useState } from "react";
import axios from "axios";
import spotifyLogo from '../../svg/Full_Logo_Black_RGB.svg';


export function ProfileCard()   {

  const [opened, { open, close }] = useDisclosure(false);
  const [profilePicture, setProfilePicture] = useState<string>("https://raw.githubusercontent.com/mantinedev/mantine/master/.demo/images/bg-8.png"); 
  const [username, setUsername] = useState<string>("");

  const handleProfilePictureChange = (newPictureUrl: string) => {
    setProfilePicture(newPictureUrl);
    axios.post(`http://localhost:9191/user/profile`, null, { 
      headers: {
        Authorization: `Bearer ${localStorage.getItem('jwtToken')}`
      },
      params: {
        url: newPictureUrl 
      }
    })
    .then((response) => {
      console.log("Profile picture updated:", response);
    })
    .catch((error) => {
      console.error('Error updating profile picture:', error);
    });
  };

  useEffect(() => {
    axios.get('http://localhost:9191/user/profile', {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('jwtToken')}`
      }
    })
    .then((response) => {
      console.log(response.data);
      setProfilePicture(response.data.url);
      setUsername(response.data.user.email);
    })
    .catch((error) => {
      console.error('Error fetching profile picture:', error);
    });

  }, []);


    return (
        <>
        <Container>
        <Paper h={300} w={450} shadow="md" p="md" radius="md" mt={30} style={{ backgroundColor: '#D6ECFB' }}>

            <Stack justify="center" align="center">
              {profilePicture !== 'https://raw.githubusercontent.com/mantinedev/mantine/master/.demo/images/bg-8.png' && 
              <img src={spotifyLogo} alt="Vinyl Logo" width={70}  /> }

        <AspectRatio ratio={1}>
                <Image
                onClick={open}
                  w="150px"
                  src={profilePicture}
                  radius="4px"
                  alt="profile pic"
                />
              </AspectRatio>
              </Stack>
              <Group>
              <Text ta="left" size="15px"  mt="md" fw={700} >Username: </Text>
              <Text size="15px"  mt="md" >{username.slice(0, username.indexOf('@'))}</Text>
              </Group>

        </Paper>
              </Container>
              <SearchModal isOpen={opened} onClose={close} onAlbumSelect={handleProfilePictureChange} source="profile" />
        </>
    )
}