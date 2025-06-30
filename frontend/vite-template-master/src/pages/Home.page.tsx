import { Welcome } from '../components/Welcome/Welcome';
import { ColorSchemeToggle } from '../components/ColorSchemeToggle/ColorSchemeToggle';
import {AuthenticationForm} from '../components/Login/Login'
import { SimpleGrid, Flex } from '@mantine/core';

export function HomePage() {
  return (
    <>
      <AuthenticationForm  />
    </>
  );
}
