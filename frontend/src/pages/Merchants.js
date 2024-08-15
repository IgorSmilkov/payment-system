import React, { useEffect, useState } from 'react';
import Box from '@mui/material/Box';
import Snackbar from '@mui/material/Snackbar';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/DeleteOutlined';
import SaveIcon from '@mui/icons-material/Save';
import CancelIcon from '@mui/icons-material/Close';
import {
  GridRowModes,
  DataGrid,
  GridActionsCellItem,
  GridRowEditStopReasons,
} from '@mui/x-data-grid';
import api from '../services/api';

const Merchants = () => {
  const [rows, setRows] = useState([]);
  const [rowModesModel, setRowModesModel] = useState({});
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'error' });
  const [loading, setLoading] = useState(false);
  const [paginationModel, setPaginationModel] = useState({
    page: 0,
    pageSize: 10,
  });
  const [rowCount, setRowCount] = useState(0);

  const updateMerchant = async (id, updateData) => {
    try {
      const response = await api.put(`/api/v1/merchants/${id}`, updateData);
      return response.data;
    } catch (error) {
      console.error('Error updating merchant:', error);
      throw error;
    }
  };

  const deleteMerchant = async (id) => {
    try {
      await api.delete(`/api/v1/merchants/${id}`);
    } catch (error) {
      console.error('Error deleting merchant:', error);
      throw error;
    }
  };

  const fetchMerchants = async (page, pageSize) => {
    setLoading(true);
    try {
      const response = await api.get('/api/v1/merchants', {
        params: {
          page: page,
          size: pageSize,
        },
      });

      setRows(response.data.content);
      setRowCount(response.data.totalElements);
    } catch (error) {
      console.error('Error fetching merchants:', error);
      setSnackbar({ open: true, message: 'Failed to fetch merchants.', severity: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMerchants(paginationModel.page, paginationModel.pageSize);
  }, [paginationModel]);

  const handleRowEditStop = (params, event) => {
    if (params.reason === GridRowEditStopReasons.rowFocusOut) {
      event.defaultMuiPrevented = true;
    }
  };

  const handleEditClick = (id) => () => {
    setRowModesModel({ ...rowModesModel, [id]: { mode: GridRowModes.Edit } });
  };

  const handleSaveClick = async (updatedRow) => {
    try {
      const result = await updateMerchant(updatedRow.id, updatedRow);
      setRows((prevRows) =>
        prevRows.map((row) => (row.id === updatedRow.id ? result : row))
      );
      setRowModesModel({ ...rowModesModel, [updatedRow.id]: { mode: GridRowModes.View } });
    } catch (error) {
      console.error('Failed to save merchant:', error);
      setSnackbar({ open: true, message: 'Failed to save merchant.', severity: 'error' });
    }
  };

  const handleDeleteClick = (id) => async () => {
    try {
      await deleteMerchant(id);
      fetchMerchants(paginationModel.page, paginationModel.pageSize);
    } catch (error) {
      console.error('Failed to delete merchant:', error);
      const errorMessage =
        error.response && error.response.data && error.response.data.message
          ? error.response.data.message
          : 'An unexpected error occurred while deleting the merchant.';

      setSnackbar({ open: true, message: `Error: ${errorMessage}`, severity: 'error' });
    }
  };

  const handleCancelClick = (id) => () => {
    setRowModesModel({
      ...rowModesModel,
      [id]: { mode: GridRowModes.View, ignoreModifications: true },
    });

    const editedRow = rows.find((row) => row.id === id);
    if (editedRow.isNew) {
      setRows((prevRows) => prevRows.filter((row) => row.id !== id));
    }
  };

  const processRowUpdate = (newRow) => {
    const updatedRow = { ...newRow, isNew: false };
    setRows((prevRows) => prevRows.map((row) => (row.id === newRow.id ? updatedRow : row)));

    handleSaveClick(updatedRow);

    return updatedRow;
  };

  const handleRowModesModelChange = (newRowModesModel) => {
    setRowModesModel(newRowModesModel);
  };

  const handleCloseSnackbar = () => {
    setSnackbar({ ...snackbar, open: false });
  };

  const columns = [
    { field: 'name', headerName: 'Name', width: 180, editable: true },
    {
      field: 'description',
      headerName: 'Description',
      width: 220,
      editable: true,
    },
    {
      field: 'status',
      headerName: 'Status',
      width: 150,
      editable: true,
      type: 'singleSelect',
      valueOptions: ['ACTIVE', 'INACTIVE'],
    },
    {
      field: 'actions',
      type: 'actions',
      headerName: 'Actions',
      width: 100,
      cellClassName: 'actions',
      getActions: ({ id }) => {
        const isInEditMode = rowModesModel[id]?.mode === GridRowModes.Edit;

        if (isInEditMode) {
          return [
            <GridActionsCellItem
              icon={<SaveIcon />}
              label="Save"
              sx={{
                color: 'primary.main',
              }}
              onClick={() => handleSaveClick(rows.find((row) => row.id === id))}
            />,
            <GridActionsCellItem
              icon={<CancelIcon />}
              label="Cancel"
              className="textPrimary"
              onClick={handleCancelClick(id)}
              color="inherit"
            />,
          ];
        }

        return [
          <GridActionsCellItem
            icon={<EditIcon />}
            label="Edit"
            className="textPrimary"
            onClick={handleEditClick(id)}
            color="inherit"
          />,
          <GridActionsCellItem
            icon={<DeleteIcon />}
            label="Delete"
            onClick={handleDeleteClick(id)}
            color="inherit"
          />,
        ];
      },
    },
  ];

  return (
    <Box
      sx={{
        height: 600,
        width: '100%',
        padding: 2,
        '& .actions': {
          color: 'text.secondary',
        },
        '& .textPrimary': {
          color: 'text.primary',
        },
      }}
    >
      <DataGrid
        rows={rows}
        columns={columns}
        pagination
        paginationMode="server"
        rowCount={rowCount}
        loading={loading}
        paginationModel={paginationModel}
        onPaginationModelChange={(newModel) => setPaginationModel(newModel)}
        pageSizeOptions={[10, 20, 50, 100]}
        editMode="row"
        rowModesModel={rowModesModel}
        onRowModesModelChange={handleRowModesModelChange}
        onRowEditStop={handleRowEditStop}
        processRowUpdate={processRowUpdate}
      />
      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={handleCloseSnackbar}
      >
        <Alert onClose={handleCloseSnackbar} severity={snackbar.severity} sx={{ width: '100%' }}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default Merchants;
