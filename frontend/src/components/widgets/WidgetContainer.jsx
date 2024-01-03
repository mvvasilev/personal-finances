import Card from "@mui/material/Card";
import * as React from "react";
import Divider from "@mui/material/Divider";
import Typography from "@mui/material/Typography";
import Grid from "@mui/material/Unstable_Grid2";
import {Close, Edit} from "@mui/icons-material";
import {IconButton} from "@mui/material";
import {useEffect, useState} from "react";
import utils from "@/utils.js";
import { PARAMS } from "@/components/widgets/WidgetParameters.js";
import dayjs from "dayjs";
import 'chart.js/auto';
import {Line, Pie} from 'react-chartjs-2';

export default function WidgetContainer({widget, sx, onEdit, onRemove}) {

    const [data, setData] = useState(null);

    useEffect(() => {
        var queryString = "";

        queryString += widget.parameters?.filter(p => p.name.includes(PARAMS.CATEGORY_PREFIX)).map(c => `categoryId=${c.numericValue}`)?.join("&");

        let isToNow = widget.parameters?.find(p => p.name === PARAMS.IS_TO_NOW)?.booleanValue ?? false;
        let isFromStatic = widget.parameters?.find(p => p.name === PARAMS.IS_FROM_DATE_STATIC)?.booleanValue ?? false;

        var fromDate;
        var toDate;

        if (isToNow) {
            toDate = dayjs();
        } else {
            toDate = dayjs(widget.parameters?.find(p => p.name === PARAMS.TO_DATE)?.timestampValue);
        }

        if (!isFromStatic) {
            fromDate = dayjs().subtract(widget.parameters?.find(p => p.name === PARAMS.RELATIVE_FROM_PERIOD)?.numericValue ?? 30, 'days');
        } else {
            fromDate = dayjs(widget.parameters?.find(p => p.name === PARAMS.FROM_DATE)?.timestampValue);
        }

        var includeUncategorized = widget.parameters?.find(p => p.name === PARAMS.INCLUDE_UNCATEGORIZED)?.booleanValue ?? false;

        queryString += `&from=${fromDate.toISOString()}`;
        queryString += `&to=${toDate.toISOString()}`;
        queryString += `&includeUncategorized=${includeUncategorized}`;

        switch (widget.type) {
            case "TOTAL_SPENDING_PER_CATEGORY": {
                utils.performRequest(`/api/statistics/totalSpendingByCategory?${queryString}`)
                    .then(resp => resp.json())
                    .then(resp => setData(resp.result));

                break;
            }
            case "SPENDING_OVER_TIME_PER_CATEGORY": {
                queryString += widget.parameters?.find(p => p.name === PARAMS.TIME_PERIOD)?.stringValue ?? "DAILY";

                utils.performRequest(`/api/statistics/spendingOverTimeByCategory?${queryString}`)
                    .then(resp => resp.json())
                    .then(resp => setData(resp.result));

                break;
            }
            case "SUM_PER_CATEGORY": {
                utils.performRequest(`/api/statistics/sumByCategory?${queryString}`)
                    .then(resp => resp.json())
                    .then(resp => setData(resp.result));

                break;
            }
        }
    }, [widget]);

    return (
        <Card
            sx={{
                width: "100%",
                height: "100%",
                p: 2,
                ...sx
            }}
        >
            <Grid container>
                <Grid container xs={12} lg={12}>
                    <Grid xs={10} lg={10}>
                        <Typography
                            sx={{
                                width: "100%",
                                fontSize: "1.4em"
                            }}
                        >
                            { widget.name }
                        </Typography>
                    </Grid>
                    <Grid xs={1} lg={1}>
                        <IconButton
                            sx={{ width: "100%" }}
                            onClick={onEdit}
                            className={"grid-drag-cancel"}
                        >
                            <Edit />
                        </IconButton>
                    </Grid>
                    <Grid xs={1} lg={1}>
                        <IconButton
                            sx={{ width: "100%" }}
                            onClick={onRemove}
                            className={"grid-drag-cancel"}
                        >
                            <Close />
                        </IconButton>
                    </Grid>
                </Grid>
                <Grid xs={12} lg={12}>
                    <Divider></Divider>
                </Grid>
                <Grid xs={12} lg={12}>
                    <div className={"grid-drag-cancel"} style={{ position: "relative", height: "100%", width: "100%" }}>
                        {
                            !utils.isNullOrUndefined(data) && widget.type === "TOTAL_SPENDING_PER_CATEGORY" &&
                            <Pie
                                options={{
                                    responsive: true,
                                    maintainAspectRatio: true,
                                    aspectRatio: 1
                                }}
                                data={{
                                    labels: data.categories.map(c => c.name),
                                    datasets: [
                                        {
                                            label: "Amount",
                                            data: data.categories.map(c => data.spendingByCategory[c.id])
                                        }
                                    ]
                                }}
                            />
                        }
                        {
                            !utils.isNullOrUndefined(data) && widget.type === "SPENDING_OVER_TIME_PER_CATEGORY" &&
                            <Line />
                        }
                        {
                            !utils.isNullOrUndefined(data) && widget.type === "SUM_PER_CATEGORY" &&
                            <Typography sx={{
                                fontSize: "2.3em"
                            }}>
                                { utils.formatCurrency(data) }
                            </Typography>
                        }
                    </div>
                </Grid>
            </Grid>
        </Card>
    );
}