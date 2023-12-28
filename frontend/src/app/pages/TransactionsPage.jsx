import Grid from "@mui/material/Unstable_Grid2";
import {Stack} from "@mui/material";
import {useEffect, useState} from "react";
import {DataGrid} from "@mui/x-data-grid";
import utils from "@/utils.js";

const COLUMNS = [
    {
        field: "isInflow",
        headerName: "Inflow/Outflow",
        type: "boolean",
        width: 150,
        sortable: false,
        filterable: false,
    },
    {
        field: "amount",
        headerName: "Amount",
        type: "currency",
        maxWidth: 150,
        flex: true,
        sortable: true,
        filterable: false,
        valueFormatter: val => `${val.value} лв.`
    },
    {
        field: "description",
        headerName: "Description",
        type: "string",
        minWidth: 150,
        flex: true,
        sortable: true,
        filterable: false,
    },
    {
        field: "timestamp",
        headerName: "Timestamp",
        type: "datetime",
        maxWidth: 250,
        flex: true,
        sortable: true,
        filterable: false,
        valueFormatter: val => new Date(val.value).toLocaleString('en-UK')
    },
];

export default function TransactionsPage() {

    const [pageOptions, setPageOptions] = useState({
        page: 0,
        pageSize: 100,
    });

    const [sortOptions, setSortOptions] = useState([
        {
            field: "timestamp",
            sort: "asc"
        }
    ]);

    const [transactions, setTransactions] = useState({});

    useEffect(() => {
        // Multi-sorting requires the MUI data grid pro license :)
        let sortBy = sortOptions.map((sort) => `&sort=${sort.field},${sort.sort}`).join("")

        console.log(sortBy)

        utils.performRequest(`/api/processed-transactions?page=${pageOptions.page}&size=${pageOptions.pageSize}${sortBy}`)
            .then(resp => resp.json())
            .then(({result}) => setTransactions(result));
    }, [pageOptions, sortOptions]);

    return (
        <Stack>
            <Grid container columnSpacing={1}>
                <Grid xs={12} lg={12}>
                    <DataGrid
                        columns={COLUMNS}
                        rows={transactions.content ?? []}
                        rowCount={transactions.totalElements ?? 0}
                        paginationMode={"server"}
                        sortingMode={"server"}
                        paginationModel={pageOptions}
                        onPaginationModelChange={(model, details) => setPageOptions(model)}
                        sortModel={sortOptions}
                        onSortModelChange={(model, change) => setSortOptions(model)}
                    />
                </Grid>
            </Grid>
        </Stack>
    );
}