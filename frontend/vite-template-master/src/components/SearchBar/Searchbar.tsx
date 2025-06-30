import { Autocomplete, Box, Button, rem, Text, TextInput, UnstyledButton } from "@mantine/core";
import { IconSearch } from "@tabler/icons-react";
import { Spotlight, spotlight } from '@mantine/spotlight';
import { useDisclosure } from "@mantine/hooks";
import { SearchModal } from "./SearchbarModal";


export function Searchbar() {
  const [opened, { open, close }] = useDisclosure(false);

  return (
    <>

      <UnstyledButton
        onClick={open} 
        style={{
          width: '200px',
          height: '35px',
          padding: '8px 12px',
          borderRadius: '8px',
          border: '1px solid #ced4da',
          display: 'flex',
          alignItems: 'center',
          backgroundColor: '#fff',
          position: 'sticky', 
          top: '20px', 
          zIndex: 10, 
          boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)'
        }}
      >
        <IconSearch style={{ width: rem(17), height: rem(17), marginRight: '8px' }} stroke={1.5} />
        <Text style={{ color: '#adb5bd' }}>Search for music...</Text>
      </UnstyledButton>

      <SearchModal isOpen={opened} onClose={close} />


    </>
  );
}
