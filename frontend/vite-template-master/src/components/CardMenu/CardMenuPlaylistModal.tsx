import { AspectRatio, Card, Container, Grid, Menu, Modal, Table, Notification, Text, Center } from "@mantine/core";
import { IconCheck } from "@tabler/icons-react";
import axios from "axios";
import { useEffect, useState } from "react";

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
}







export function CardMenuPlaylistModal({ isOpen, onClose, track, artist}: { isOpen: boolean, onClose: () => void, track: any, artist: string }) {
  const [playlists, setPlaylists] = useState<Playlist[]>([]);


  useEffect(() => {
    if (isOpen) {
      axios.get('http://localhost:9191/playlists', {
        headers: { Authorization: `Bearer ${localStorage.getItem('jwtToken')}` }
      }).then((response) => {
        setPlaylists(response.data);
      }).catch((error) => {
        console.error("Error fetching playlists", error);
      });
    }
  }, [isOpen]);

  function handleAddToPlaylist(playlistId: number) {
    console.log("Adding track to playlist", playlistId);
    console.log("Adding track id to playlist", track.id);
    axios.post('http://localhost:9191/playlists/add', 
       null ,
      {
        headers: { Authorization: `Bearer ${localStorage.getItem('jwtToken')}` },
        params: { SpotifyId: track.id, playlistId: playlistId }
      }
    ).then(() => {
      onClose();
    }).catch((error) => {
      console.error("Error adding track to playlist", error);
    });




  }

  const rows = playlists.map((playlist) => (
    <>
    <Table.Tr key={playlist.id} onClick={() => handleAddToPlaylist(playlist.id)} >
      <Table.Td>
        {playlist.name}
        </Table.Td>
      <Table.Td >{playlist.createdAt}</Table.Td>
      <Table.Td >{playlist.numberOfTracks}</Table.Td>
    </Table.Tr>
    </>
  ));

  return (
    <Modal opened={isOpen} onClose={onClose} centered size="lg">
      {playlists.length === 0 ? (
        <Text ta="center">No playlists found.</Text>
      ) : (
        <Table highlightOnHover highlightOnHoverColor='#dce5f5'>
          <Table.Thead>
            <Table.Tr>
              <Table.Th>Name</Table.Th>
              <Table.Th>Date Created</Table.Th>
              <Table.Th>Track Count</Table.Th>
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>{rows}</Table.Tbody>
        </Table>
      )}
    </Modal>
  );
}