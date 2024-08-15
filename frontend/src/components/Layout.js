import React from 'react';
import { Grid } from '@mui/material';
import Sidebar from './Sidebar';
import Header from './Header';

const Layout = ({ children, userInfo }) => {
  return (
    <Grid container>
      <Grid item xs={12}>
        <Header />
      </Grid>
      <Grid container>
        <Grid item xs={2}>
          <Sidebar userInfo={userInfo} />
        </Grid>
        <Grid item xs={10}>
          <main>{children}</main>
        </Grid>
      </Grid>
    </Grid>
  );
};

export default Layout;
