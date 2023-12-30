import Grid from "@mui/material/Unstable_Grid2";
import {Stack} from "@mui/material";
import {useEffect, useState} from "react";
import {DataGrid} from "@mui/x-data-grid";
import utils from "@/utils.js";
import {ArrowDownward, ArrowUpward, PriceChange} from "@mui/icons-material";
import CategoriesBox from "@/components/categories/CategoriesBox.jsx";

const COLUMNS = [
    {
        field: "isInflow",
        headerName: "Inflow/Outflow",
        type: "boolean",
        width: 150,
        sortable: false,
        filterable: false,
        renderCell: (params) => {
            return params.value ? (
                <>
                    <PriceChange style={{ color: '#4d4' }} />
                    <ArrowUpward style={{ color: '#4d4' }} />
                </>
            ) : (
                <>
                    <PriceChange style={{ color: '#d44' }} />
                    <ArrowDownward style={{ color: '#d44' }} />
                </>
            );
        }
    },
    {
        field: "amount",
        headerName: "Amount",
        type: "currency",
        maxWidth: 150,
        flex: true,
        sortable: true,
        filterable: false,
        valueFormatter: val => `${(val.value).toLocaleString(undefined, { minimumFractionDigits: 2 })} лв.`
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
        valueFormatter: val => new Date(val.value).toLocaleString("bg-BG")
    },
    {
        field: "categories",
        headerName: "Categories",
        maxWidth: 300,
        flex: true,
        sortable: false,
        filterable: false,
        renderCell: (params) => {
            return (
                <CategoriesBox
                    categories={params.value}
                    minHeight={0}
                    selectable={false}
                />
            );
        }
    }
];

export default function TransactionsPage() {

    const [pageOptions, setPageOptions] = useState({
        page: 0,
        pageSize: 50,
    });

    const [sortOptions, setSortOptions] = useState([
        {
            field: "timestamp",
            sort: "asc"
        }
    ]);

    const [transactions, setTransactions] = useState({});

    useEffect(() => {
        utils.showSpinner();

        // Multi-sorting requires the MUI data grid pro license :)
        let sortBy = sortOptions.map((sort) => `&sort=${sort.field},${sort.sort}`).join("")

        console.log(sortBy)

        utils.performRequest(`/api/processed-transactions?page=${pageOptions.page}&size=${pageOptions.pageSize}${sortBy}`)
            .then(resp => resp.json())
            .then(({result}) => {
                setTransactions(result);
                utils.hideSpinner();
            });
    }, [pageOptions, sortOptions]);

    return (
        <Stack
        >
            <Grid
                container
                columnSpacing={1}
            >
                <Grid
                    sx={{
                        height: "95vh"
                    }}
                    xs={12}
                    lg={12}
                >
                    <DataGrid
                        sx={{
                            overflowY: "scroll"
                        }}
                        columns={COLUMNS}
                        rows={transactions.content ?? []}
                        rowCount={transactions.totalElements ?? 0}
                        pageSizeOptions={[25, 50, 100]}
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