import Box from "@mui/material/Box";
import Divider from "@mui/material/Divider";
import Grid from "@mui/material/Unstable_Grid2";
import {
    Checkbox,
    FormControlLabel,
    Menu,
    MenuItem,
    Modal,
    OutlinedInput,
    Select,
    Slider,
    TextField
} from "@mui/material";
import Typography from "@mui/material/Typography";
import utils from "@/utils.js";
import {DatePicker} from "@mui/x-date-pickers";
import dayjs from "dayjs";
import Button from "@mui/material/Button";
import {Close as CloseIcon, Save as SaveIcon} from "@mui/icons-material";
import * as React from "react";
import {useEffect, useState} from "react";
import { PARAMS } from "@/components/widgets/WidgetParameters.js";
import Card from "@mui/material/Card";

export default function WidgetEditModal(
    {
        initialWidget,
        onCreate,
        onEdit,
        open,
        setOpen
    }
) {

    const [widget, setWidget] = useState({});
    const [widgetTypes, setWidgetTypes] = useState([]);
    const [categories, setCategories] = useState([]);
    const [timePeriods, setTimePeriods] = useState([]);

    useEffect(() => {
        setWidget({
            ...initialWidget,
            parameters: initialWidget.parameters?.reduce((acc, item) => {
                acc[item.name] = { ...item, name: undefined };
                return acc;
            }, {}) ?? {}
        });
    }, [initialWidget]);

    useEffect(() => {
        utils.performRequest("/api/widgets/types")
            .then(resp => resp.json())
            .then(resp => setWidgetTypes(resp.result));

        utils.performRequest("/api/categories")
            .then(resp => resp.json())
            .then(resp => setCategories(resp.result));

        utils.performRequest("/api/statistics/timePeriods")
            .then(resp => resp.json())
            .then(resp => setTimePeriods(resp.result));
    }, []);

    useEffect(() => {
        console.log(widget);
    }, [widget]);

    function widgetParams(name, defaultValue) {
        if (!widget.parameters) {
            widget.parameters = {};
        }

        let val = widget.parameters[name];

        if (val) {
            return val;
        }

        let newVal = {};

        defaultValue(newVal);

        widget.parameters[name] = newVal;

        setWidget({...widget});

        return newVal;
    }

    function widgetParamsMultiselect(prefix, fetchValue) {
        return Object.entries(widget.parameters)
            .filter(([name, value]) => name.startsWith(prefix))
            .map(([name, value]) => fetchValue(value));
    }

    function setWidgetParam(name, setParam) {
        var param = widget.parameters[name];

        if (!param) {
            widget.parameters = {...widget.parameters};
            widget.parameters[name] = {};
            param = widget.parameters[name];
        }

        setParam(param);
        setWidget({...widget});
    }

    function setWidgetParamsMultiselect(prefix, selected, setParam) {
        // First, clear the current parameters of all elements with the prefix
        // Then, re-insert the selected elements

        widget.parameters = Object.entries(widget.parameters)
            .filter(([name, value]) => !name.startsWith(prefix))
            .reduce((acc, item) => {
                acc[item.name] = { ...item, name: undefined };
                return acc;
            });

        selected.forEach((value, i) => {
            widget.parameters[`${prefix}-${i}`] = {};
            setParam(widget.parameters[`${prefix}-${i}`], value);
        })

        setWidget({...widget});
    }

    function mapWidget() {
        // widget.parameters = widget.parameters.concat(widget.selectedCategories?.map((c, i) => {
        //     return {
        //         name: `${PARAMS.CATEGORY_PREFIX}-${i}`,
        //         numericValue: c
        //     }
        // }));

        // widget.selectedCategories = undefined;

        return {
            ...widget,
            selectedCategories: undefined,
            parameters: Object.entries(widget.parameters).map(([name, value]) => {
                return {
                    ...value,
                    name: name
                }
            })
            //     .concat(widget.selectedCategories?.map((c, i) => {
            //     return {
            //         name: `${PARAMS.CATEGORY_PREFIX}-${i}`,
            //         numericValue: c
            //     }
            // }))
        }
    }

    function onEditWidget() {
        onEdit(mapWidget());
    }

    function onCreateWidget() {
        onCreate(mapWidget());
    }

    return (
        widget &&
        <Modal
            open={open}
        >
            <Card
                sx={{
                    position: "absolute",
                    top: "50%",
                    left: "50%",
                    transform: 'translate(-50%, -50%)',
                    width: 400,
                    height: "fit-content",
                    p: 4
                }}
            >
                {
                    widget.dbId ? (
                        <Typography sx={{ pb: 1, fontSize: "1.25em"}}>Editing Widget</Typography>
                    ) : (
                        <Typography sx={{ pb: 1, fontSize: "1.25em"}}>Create New Widget</Typography>
                    )
                }
                <Divider></Divider>
                <Grid container spacing={1}>
                    <Grid xs={12} lg={12}>
                        <TextField
                            id="widget-name"
                            label="Name"
                            variant="outlined"
                            value={widget.name ?? ""}
                            onChange={(e) => setWidget({
                                ...widget,
                                name: e.target.value
                            })}
                            autoFocus
                            sx={{width: "100%"}}
                        />
                    </Grid>
                    <Grid xs={12} lg={12}>
                        <Select
                            id={"widget-type"}
                            sx={{ width: "100%" }}
                            value={widget.type ?? "placeholder"}
                            onChange={(e) => setWidget({
                                ...widget,
                                type: e.target.value
                            })}
                        >
                            <MenuItem value={"placeholder"} disabled>
                                <Typography sx={{ color: 'gray' }}>Type</Typography>
                            </MenuItem>
                            {
                                widgetTypes.map(wt => (
                                    <MenuItem
                                        key={wt}
                                        value={wt}
                                    >
                                        <Typography>{ utils.toPascalCase(wt.replace(/_/g, " ")) }</Typography>
                                    </MenuItem>
                                ))
                            }
                        </Select>
                    </Grid>
                    <Grid xs={8} lg={8}>
                        {
                            widgetParams(PARAMS.IS_FROM_DATE_STATIC, val => val.booleanValue = false)?.booleanValue ?? false ? (
                                <DatePicker
                                    sx={{ width: "100%", height: "100%" }}
                                    label="From"
                                    value={dayjs(widgetParams(PARAMS.FROM_DATE, val => val.timestampValue = dayjs())?.timestampValue)}
                                    onChange={(newValue) => {
                                        setWidgetParam(PARAMS.FROM_DATE, p => p.timestampValue = newValue);
                                    }}
                                />
                            ) : (
                                <Slider
                                    sx={{ width: "98%" }}
                                    label={"From"}
                                    min={-365}
                                    scale={x => -x}
                                    max={-7}
                                    track="inverted"
                                    valueLabelDisplay="auto"
                                    valueLabelFormat={(val) => `${val} days`}
                                    value={-widgetParams(PARAMS.RELATIVE_FROM_PERIOD, (val) => val.numericValue = 30)?.numericValue}
                                    onChange={(e, newVal) => {
                                        setWidgetParam(PARAMS.RELATIVE_FROM_PERIOD, (p) => p.numericValue = -newVal);
                                        setWidget({...widget});
                                    }}
                                />
                            )
                        }
                    </Grid>
                    <Grid xs={4} lg={4}>
                        <FormControlLabel
                            sx={{ width: "100%", height: "100%" }}
                            value="end"
                            control={
                                <Checkbox
                                    checked={widgetParams(PARAMS.IS_FROM_DATE_STATIC, val => val.booleanValue = false)?.booleanValue}
                                    onChange={(e) => {
                                        setWidgetParam(PARAMS.IS_FROM_DATE_STATIC, p => p.booleanValue = e.target.checked);
                                    }}
                                />
                            }
                            label="Static"
                            labelPlacement="end"
                        />
                    </Grid>
                    <Grid xs={8} lg={8}>
                        <DatePicker
                            sx={{ width: "100%", height: "100%" }}
                            label="To"
                            value={dayjs(widgetParams(PARAMS.TO_DATE, val => val.timestampValue = dayjs())?.timestampValue)}
                            disabled={widgetParams(PARAMS.IS_TO_NOW, val => val.booleanValue = true)?.booleanValue}
                            onChange={(newValue) => {
                                setWidgetParam(PARAMS.TO_DATE, p => p.timestampValue = newValue);
                            }}
                        />
                    </Grid>
                    <Grid xs={4} lg={4}>
                        <FormControlLabel
                            sx={{ width: "100%", height: "100%" }}
                            value="end"
                            control={
                                <Checkbox
                                    checked={widgetParams(PARAMS.IS_TO_NOW, val => val.booleanValue = true)?.booleanValue}
                                    onChange={(e) => {
                                        setWidgetParam(PARAMS.IS_TO_NOW, p => p.booleanValue = e.target.checked);
                                    }}
                                />
                            }
                            label="To Now"
                            labelPlacement="end"
                        />
                    </Grid>
                    {
                        widget.type === "SPENDING_OVER_TIME_PER_CATEGORY" &&
                        <Grid xs={12} lg={12}>
                            <Select
                                id={"time-period-type"}
                                sx={{
                                    width: "100%"
                                }}
                                value={widgetParams(PARAMS.TIME_PERIOD, val => val.stringValue = "placeholder")?.stringValue}
                                onChange={(e) => {
                                    setWidgetParam(PARAMS.TIME_PERIOD, p => p.stringValue = e.target.value);
                                }}
                            >
                                <MenuItem value={"placeholder"} disabled>
                                    <Typography sx={{ color: 'gray' }}>Time Period</Typography>
                                </MenuItem>
                                {
                                    timePeriods.map(wt => (
                                        <MenuItem
                                            key={wt}
                                            value={wt}
                                        >
                                            <Typography>{ utils.toPascalCase(wt.replace(/_/g, " ")) }</Typography>
                                        </MenuItem>
                                    ))
                                }
                            </Select>
                        </Grid>
                    }
                    <Grid xs={12} lg={12}>
                        <FormControlLabel
                            sx={{ width: "100%", height: "100%" }}
                            value="end"
                            control={
                                <Checkbox
                                    checked={widgetParams(PARAMS.INCLUDE_UNCATEGORIZED, val => val.booleanValue = false)?.booleanValue}
                                    onChange={(e) => {
                                        setWidgetParam(PARAMS.INCLUDE_UNCATEGORIZED, p => p.booleanValue = e.target.checked);
                                    }}
                                />
                            }
                            label="Include Uncategorized"
                            labelPlacement="end"
                        />
                    </Grid>
                    <Grid xs={12} lg={12}>
                        <Select
                            sx={{ width: "100%", height: "100%" }}
                            input={<OutlinedInput label="Categories" />}
                            multiple
                            value={widgetParamsMultiselect(PARAMS.CATEGORY_PREFIX, (v) => v.numericValue) ?? []}
                            renderValue={(selected) => {
                                return (<Typography>
                                    { selected.map(s => categories.find(c => c.id === s)?.name)?.join(", ")}
                                </Typography>)
                            }}
                            onChange={(e) => {
                                setWidgetParamsMultiselect(PARAMS.CATEGORY_PREFIX, e.target.value, (param, value) => param.numericValue = value);
                            }}
                        >
                            <MenuItem value="placeholder" disabled>
                                <Typography sx={{ color: 'gray' }}>Categories</Typography>
                            </MenuItem>
                            {
                                categories.map(c => (
                                    <MenuItem key={c.id} value={c.id}>
                                        <Checkbox checked={widgetParamsMultiselect(PARAMS.CATEGORY_PREFIX, (v) => v.numericValue)?.findIndex(cat => cat === c.id) > -1} />
                                        <Typography>{ c.name }</Typography>
                                    </MenuItem>
                                ))
                            }
                        </Select>
                    </Grid>

                    <Grid xs={6} lg={6}>
                        {
                            widget.dbId ? (
                                <Button
                                    sx={{width: "100%"}}
                                    variant="contained"
                                    onClick={onEditWidget}
                                    startIcon={<SaveIcon />}
                                >
                                    Update
                                </Button>
                            ) : (
                                <Button
                                    sx={{width: "100%"}}
                                    variant="contained"
                                    onClick={onCreateWidget}
                                    startIcon={<SaveIcon />}
                                >
                                    Create
                                </Button>
                            )
                        }
                    </Grid>
                    <Grid xs={6} lg={6}>
                        <Button
                            sx={{width: "100%"}}
                            onClick={() => setOpen(false)}
                            startIcon={<CloseIcon />}
                        >
                            Cancel
                        </Button>
                    </Grid>
                </Grid>
            </Card>
        </Modal>
    );
}