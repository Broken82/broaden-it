
import React from 'react';

import { TextInput, Modal, Center, Container, Button } from '@mantine/core';
import { useForm } from '@mantine/form';
import axios from 'axios';

export function PlaylistCreateModal({ isOpen, onClose, fetchPlaylists }: { isOpen: boolean, onClose: () => void, fetchPlaylists: () => void }) {

    const form = useForm({
        initialValues: {
          name: '',
        },
    
        validate: {
          name: (val) => (val.length === 0 ? 'Name can not be empty' : null),
        },
      });

      function handlePlaylistCreate() {
        axios.post('http://localhost:9191/playlists/create', null, {
            params: { name: form.values.name },
            headers: { Authorization: `Bearer ${localStorage.getItem('jwtToken')}` }
        })
        .then((response) => {
            console.log(response);
            fetchPlaylists();  
        })
        .catch((error) => {
            console.error('Error creating playlist:', error);
        });
        onClose();
    }


    return(
        <>
        <Modal opened={isOpen} onClose={onClose} title="Add new playlist" centered size="lg">
        
        <form onSubmit={form.onSubmit(handlePlaylistCreate)}> 
        <Container size={350}>
           
        <TextInput
            label="Name"
            placeholder="Playlist name"
            value={form.values.name}
            error={form.errors.name && 'Name can not be empty'}
            onChange={(event) => form.setFieldValue('name', event.currentTarget.value)}
        />
        </Container>
        <Center>
            <Button mt={10} type="submit">Add</Button>
        </Center>
        
        </form>
        </Modal>
        </>
    )

}