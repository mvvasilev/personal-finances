import * as React from 'react';
import {Link} from 'react-router-dom';
import CssBaseline from '@mui/material/CssBaseline'
import {ThemeProvider, createTheme, Container, Backdrop} from '@mui/material';
import Box from '@mui/material/Box';
import Drawer from '@mui/material/Drawer';
import Divider from '@mui/material/Divider';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import {Analytics as AnalyticsIcon} from '@mui/icons-material';
import {Home as HomeIcon} from '@mui/icons-material';
import {ReceiptLong as StatementsIcon} from '@mui/icons-material';
import {Receipt as TransactionsIcon} from '@mui/icons-material';
import {Category as CategoryIcon} from '@mui/icons-material';
import {Logout as LogoutIcon} from '@mui/icons-material';
import {Login as LoginIcon} from "@mui/icons-material";
import {Toaster} from 'react-hot-toast';
import theme from '../components/ThemeRegistry/theme';
import utils from "@/utils.js";
import {CircularProgress} from "@mui/material";
import {useEffect, useState} from "react";
import {LocalizationProvider} from "@mui/x-date-pickers";
import {AdapterDayjs} from "@mui/x-date-pickers/AdapterDayjs";

const DRAWER_WIDTH = 240;

const NAV_LINKS = [
    {text: 'Statistics', to: '/', icon: AnalyticsIcon},
    {text: 'Statements', to: '/statements', icon: StatementsIcon},
    {text: 'Categories', to: '/categories', icon: CategoryIcon},
    {text: 'Transactions', to: '/transactions', icon: TransactionsIcon},
];

const BOTTOM_LINKS = [
    // {
    //     text: 'Logout',
    //     icon: LogoutIcon,
    //     //href: "/logout",
    //     onClick: (e) => {
    //       console.log(e);
    //     }
    // },
];

function getCookie(key) {
    var b = document.cookie.match("(^|;)\\s*" + key + "\\s*=\\s*([^;]+)");
    return b ? b.pop() : "";
}

function isLoggedIn() {
    return getCookie("isLoggedIn") === "true";
}

export default function RootLayout({children}) {

    const [spinner, showSpinner] = useState(false);

    useEffect(() => {
        window.addEventListener("onSpinnerStatusChange", () => {
            showSpinner(utils.isSpinnerShown());
        })
    }, []);

    return (
        <ThemeProvider theme={theme}>
            <CssBaseline/>

            {
                <Backdrop
                    sx={{
                        color: '#fff',
                        zIndex: 2147483647
                    }}
                    open={spinner}
                >
                    <CircularProgress
                        sx={{
                            position: "absolute",
                            zIndex: 2147483647,
                            top: "50%",
                            left: "50%"
                        }}
                    />
                </Backdrop>
            }

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
                    {NAV_LINKS.map(({text, to, icon: Icon}) => (
                        <ListItem key={to} disablePadding>
                            <ListItemButton component={Link} to={to}>
                                <ListItemIcon>
                                    <Icon/>
                                </ListItemIcon>
                                <ListItemText primary={text}/>
                            </ListItemButton>
                        </ListItem>
                    ))}
                </List>
                <Divider sx={{mt: 'auto'}}/>
                <List>
                    {BOTTOM_LINKS.map(({text, icon: Icon, href, onClick}) => (
                        <ListItem key={text} disablePadding>
                            <ListItemButton type={"submit"} onClick={onClick} href={href}>
                                <ListItemIcon>
                                    <Icon/>
                                </ListItemIcon>
                                <ListItemText primary={text}/>
                            </ListItemButton>
                        </ListItem>
                    ))}
                    {!isLoggedIn() && <ListItem key="login-btn" disablePadding>
                        <ListItemButton component="button" href="/oauth2/authorization/authentik">
                            <ListItemIcon>
                                <LoginIcon/>
                            </ListItemIcon>
                            <ListItemText primary="Login"/>
                        </ListItemButton>
                    </ListItem>}
                    {isLoggedIn() && <form method="POST" action="/logout">
                        <ListItem key="logout-btn" disablePadding>
                            <ListItemButton component="button" type="submit">
                                <ListItemIcon>
                                    <LogoutIcon/>
                                </ListItemIcon>
                                <ListItemText primary="Logout"/>
                            </ListItemButton>
                        </ListItem>
                    </form>}
                </List>
            </Drawer>
            <LocalizationProvider dateAdapter={AdapterDayjs}>
                <Box
                    component="main"
                    sx={{
                        position: "absolute",
                        top: 0,
                        flexGrow: 1,
                        bgcolor: 'background.default',
                        left: `${DRAWER_WIDTH}px`,
                        right: 0,
                        p: 3,
                    }}
                >
                    {children}
                </Box>
            </LocalizationProvider>

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
        </ThemeProvider>
    );
}
