import React, { useState, useEffect } from 'react';
import { Table, Button, Modal, Container, Center, ActionIcon, Anchor } from '@mantine/core';
import { PlaylistModal } from './PlaylistModal'; 
import { useDisclosure } from '@mantine/hooks';
import { IconPlus, IconTrash } from '@tabler/icons-react';
import axios from 'axios';
import { PlaylistCreateModal } from './PlaylistCreateModal';


interface Playlist {
  id: number;
  name: string;
  createdAt: string;
  numberOfTracks: number;
}

export function Playlists() {
  const [selectedPlaylist, setSelectedPlaylist] = useState<Playlist | null>(null);
  const [playlists, setPlaylists] = useState<Playlist[]>([]); 
  const [opened, { open, close }] = useDisclosure(false);
  const [openedCreate, { open: openCreate, close: closeCreate }] = useDisclosure(false);

  
  async function fetchPlaylists(){
    try {
      const response = await axios.get<Playlist[]>('http://localhost:9191/playlists', {
        headers: { Authorization: `Bearer ${localStorage.getItem('jwtToken')}` }
      });
      setPlaylists(response.data);  
    } catch (error) {
      console.error('Error fetching playlists:', error);
    }
  };

async function handleDeleteClick(id: number) {

  axios.delete(`http://localhost:9191/playlists/delete/${id}`, {
    headers: { Authorization: `Bearer ${localStorage.getItem('jwtToken')}` }
  })
  .then(() => {
    fetchPlaylists();
  })
  .catch((error) => {
    console.error('Error deleting playlist:', error);
  });
}


  useEffect(() => {
    fetchPlaylists();
  }, []);  

  function handlePlaylistClick(playlist: Playlist) {
    setSelectedPlaylist(playlist);
    open();
  }

  const rows = playlists.map((playlist) => (
    <>
    <Table.Tr key={playlist.id} >
      <Table.Td onClick={() => handlePlaylistClick(playlist)}>
        {playlist.name}
        </Table.Td>
      <Table.Td onClick={() => handlePlaylistClick(playlist)} >{playlist.createdAt}</Table.Td>
      <Table.Td onClick={() => handlePlaylistClick(playlist)} >{playlist.numberOfTracks}</Table.Td>
      <Table.Td>
        <ActionIcon variant="subtle">
          <IconTrash size={20} color='red' onClick={() => handleDeleteClick(playlist.id)} />
        </ActionIcon>
        </Table.Td>
    </Table.Tr>
    </>
  ));

  return (
    <>
    <Container>
      <Center>
        <Button onClick={openCreate} size='sm' radius='md' mb={30} rightSection={<IconPlus size={18}/>}>
          Create Playlist
        </Button>
      </Center>

      <Table highlightOnHover highlightOnHoverColor='#dce5f5'>
        <Table.Thead>
          <Table.Tr>
            <Table.Th>Name</Table.Th>
            <Table.Th>Date Created</Table.Th>
            <Table.Th>Track Count</Table.Th>
            <Table.Th></Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>{rows}</Table.Tbody>
      </Table>

    </Container>

    <PlaylistModal
      isOpen={opened}
      onClose={close}
      playlistId={selectedPlaylist?.id || 0}
    />

    <PlaylistCreateModal
      isOpen={openedCreate}
      onClose={closeCreate}
      fetchPlaylists={fetchPlaylists}
      />


    </>
  );
}
