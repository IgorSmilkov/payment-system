import React from 'react';
import { List, ListItem, ListItemText } from '@mui/material';
import { Link } from 'react-router-dom';

const Sidebar = ({ userInfo }) => {
  return (
    <List component="nav">
      <ListItem>
        <ListItemText primary={`Welcome, ${userInfo.name}`} />
      </ListItem>
      <ListItem button component={Link} to="/transactions">
        <ListItemText primary="Transactions" />
      </ListItem>
      {userInfo.roles.includes('ROLE_ADMIN') && (
        <ListItem button component={Link} to="/merchants">
          <ListItemText primary="Merchants" />
        </ListItem>
      )}
    </List>
  );
};

export default Sidebar;
