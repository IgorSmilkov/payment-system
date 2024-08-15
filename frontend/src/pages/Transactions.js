import React, { useEffect, useState } from 'react';
import { DataGrid } from '@mui/x-data-grid';
import api from '../services/api';
import { Box } from '@mui/material';
import { format } from 'date-fns';

const Transactions = () => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [paginationModel, setPaginationModel] = useState({
    page: 0,
    pageSize: 10,
  });
  const [rowCount, setRowCount] = useState(0);

  const mapTransactionData = (transaction) => {
    return {
      id: transaction.id,
      customerEmail: transaction.customerEmail,
      amount: transaction.amount,
      type: transaction.type,
      status: transaction.status,
      createdDate: format(new Date(transaction.createdDate), 'yyyy-MM-dd HH:mm:ss'),
      merchantName: transaction.merchant?.name,
      merchantEmail: transaction.merchant?.email,
    };
  };

  const fetchTransactions = async (page, pageSize) => {
    setLoading(true);
    try {
      const response = await api.get('/api/v1/transactions', {
        params: {
          page: page,
          size: pageSize,
        },
      });

      const data = response.data;

      // Map data to ensure each row has a unique 'id' field
      const transactionsWithId = data.content.map(mapTransactionData);

      setTransactions(transactionsWithId);
      setRowCount(data.totalElements);
    } catch (error) {
      console.error('Error fetching transactions:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTransactions(paginationModel.page, paginationModel.pageSize);
  }, [paginationModel]);

  const columns = [
    { field: 'id', headerName: 'ID', width: 200 },
    { field: 'createdDate', headerName: 'Date', width:180 },
    { field: 'customerEmail', headerName: 'Customer Email', width: 200 },
    { field: 'amount', headerName: 'Amount', width: 80 },
    { field: 'status', headerName: 'Status', width: 100 },
    { field: 'type', headerName: 'Type', width: 150 },
    { field: 'merchantName', headerName: 'Merchant Name', width: 150 },
    { field: 'merchantEmail', headerName: 'Merchant Email', width: 150 },
  ];

  return (
    <Box sx={{ height: 600, width: '100%', padding: 2 }}>
      <DataGrid
        rows={transactions}
        columns={columns}
        pagination
        paginationMode="server"
        rowCount={rowCount}
        loading={loading}
        paginationModel={paginationModel}
        onPaginationModelChange={(newModel) => setPaginationModel(newModel)}
        pageSizeOptions={[10, 20, 50, 100]}
        disableRowSelectionOnClick
      />
    </Box>
  );
};

export default Transactions;
