import * as React from 'react';
import { Link } from 'react-router-dom';
import CssBaseline from '@mui/material/CssBaseline'
import { ThemeProvider, createTheme } from '@mui/material';
import Box from '@mui/material/Box';
import Drawer from '@mui/material/Drawer';
import Divider from '@mui/material/Divider';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import { Home as HomeIcon } from '@mui/icons-material';
import { Receipt as TransactionsIcon } from '@mui/icons-material';
import { Logout as LogoutIcon } from '@mui/icons-material';
import { Toaster } from 'react-hot-toast';
import theme from '../components/ThemeRegistry/theme';

const DRAWER_WIDTH = 240;

const NAV_LINKS = [
  { text: 'Home', to: '/', icon: HomeIcon },
  { text: 'Transactions', to: '/transactions', icon: TransactionsIcon },
];

const BOTTOM_LINKS = [
  { text: 'Logout', icon: LogoutIcon, href: "/logout" },
];

export default function RootLayout({ children }) {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Drawer
        sx={{
          width: DRAWER_WIDTH,
          flexShrink: 0,
          '& .MuiDrawer-paper': {
            width: DRAWER_WIDTH,
            boxSizing: 'border-box',
          },
        }}
        variant="permanent"
        anchor="left"
      >
        <List>
          {NAV_LINKS.map(({ text, to, icon: Icon }) => (
            <ListItem key={to} disablePadding>
              <ListItemButton component={Link} to={to}>
                <ListItemIcon>
                  <Icon />
                </ListItemIcon>
                <ListItemText primary={text} />
              </ListItemButton>
            </ListItem>
          ))}
        </List>
        <Divider sx={{ mt: 'auto' }} />
        <List>
          {BOTTOM_LINKS.map(({ text, icon: Icon, href }) => (
            <ListItem key={text} disablePadding>
              <ListItemButton href={href}>
                <ListItemIcon>
                  <Icon />
                </ListItemIcon>
                <ListItemText primary={text} />
              </ListItemButton>
            </ListItem>
          ))}
        </List>
      </Drawer>
      <Box
        component="main"
        sx={{
          position: "absolute",
          top: 0,
          flexGrow: 1,
          bgcolor: 'background.default',
          ml: `${DRAWER_WIDTH}px`,
          p: 3,
        }}
      >
        {children}
      </Box>
      <Toaster
        toastOptions={{
          success: {
            style: {
              background: '#dad7cd',
            },
          },
          error: {
            style: {
              background: '#ff8fab',
            },
          },
        }}
      />
    </ ThemeProvider>
  );
}
