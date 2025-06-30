import { useToggle, upperFirst } from '@mantine/hooks';
import { useForm } from '@mantine/form';
import { useNavigate } from 'react-router-dom';
import {
  TextInput,
  PasswordInput,
  Text,
  Title,
  Paper,
  Group,
  PaperProps,
  Button,
  Divider,
  Checkbox,
  Anchor,
  Stack,
  Notification,
} from '@mantine/core';
import classes from './Login.module.css';
import axios from 'axios';


export function AuthenticationForm() {
  const [type, toggle] = useToggle(['login', 'register']);
  const navigate = useNavigate();
  const form = useForm({
    initialValues: {
      nickname: '',
      email: '',
      password: ''
    },

    validate: {
      email: (val) => (/^\S+@\S+$/.test(val) ? null : 'Invalid email'),
    },
  });


  return (
    <div className={classes.wrapper}>
    <Paper className={classes.form} radius="s" p="xl"  >


    <Title order={2} className={classes.title} ta="center" mt="md" mb={50}>
          Welcome back to Broaden It!
        </Title>


      <form onSubmit={form.onSubmit( async (values) =>{
        console.log(values)
        if(type === 'register'){
          <>
          <Notification title="Register successful">
           Please check your email to verify your account.
           </Notification>
          </>

          try{

          
          const {nickname, email, password} = values
          const response = await axios.post('http://localhost:9191/register', {
            nickname,
            email,
            password

          });
          console.log(response.data)

        }
        catch(error: any){
          console.log('Login failed:', error.response ? error.response.data : error.message); 
        }
        }

        else{
          try{

          const {email, password} = values
          const response = await axios.post('http://localhost:9191/login', {
            email,
            password

          });
          console.log(response.data)

          const token = response.data.jwt;

          localStorage.setItem('jwtToken', token);

          navigate('/dashboard');
        }
        catch(error: any){
          console.log('Login failed:', error.response ? error.response.data : error.message); 
        }
        }
      }
      
      
      
      )}>
        <Stack justify="center">
          {type === 'register' && (
            <TextInput
              required
              size='md'
              label="Nickname"
              placeholder="Your Nickname"
              value={form.values.nickname}
              onChange={(event) => form.setFieldValue('nickname', event.currentTarget.value)}
              radius="md"
            />
          )}

          <TextInput
            size='md'
            required
            label="Email"
            placeholder="hello@mantine.dev"
            value={form.values.email}
            onChange={(event) => form.setFieldValue('email', event.currentTarget.value)}
            error={form.errors.email && 'Invalid email'}
            radius="md"
          />

          <PasswordInput
            size='md'
            required
            label="Password"
            placeholder="Your password"
            value={form.values.password}
            onChange={(event) => form.setFieldValue('password', event.currentTarget.value)}
            error={form.errors.password && 'Password should include at least 6 characters'}
            radius="md"
          />


        </Stack>

        <Group justify="space-between" mt="xl">
          <Anchor component="button" type="button" c="dimmed" onClick={() => toggle()} size="xs">
            {type === 'register'
              ? 'Already have an account? Login'
              : "Don't have an account? Register"}
          </Anchor>
          <Button type="submit" radius="xl">
            {upperFirst(type)}
          </Button>
        </Group>
      </form>
    </Paper>
    </div>
  );
}