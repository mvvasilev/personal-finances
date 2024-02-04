import * as React from 'react';
import {Responsive, WidthProvider} from "react-grid-layout";
import {
    Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle,
    Stack,
} from '@mui/material';
import Grid from "@mui/material/Unstable_Grid2";
import Button from "@mui/material/Button";
import {Addchart, Save, Warning} from "@mui/icons-material";
import WidgetContainer from "@/components/widgets/WidgetContainer.jsx";
import 'react-grid-layout/css/styles.css';
import {useEffect, useState} from "react";
import WidgetEditModal from "@/components/widgets/WidgetEditModal.jsx";
import utils from "@/utils.js";
import toast from "react-hot-toast";

const ResponsiveGridLayout = WidthProvider(Responsive);

export default function StatisticsPage() {

    const [layout, setLayout] = useState([]);

    const [widgets, setWidgets] = useState([]);
    const [editedWidget, setEditedWidget] = useState({});
    const [isWidgetModalOpen, openWidgetModal] = useState(false);
    const [isRemoveWidgetDialogShown, showRemoveWidgetDialog] = useState(false);
    const [removingWidgetId, setRemovingWidgetId] = useState(null);
    const [isUnsavedLayoutWarningShown, showUnsavedLayoutWarning] = useState(false);

    useEffect(() => {
        fetchWidgets();
    }, []);

    function fetchWidgets() {
        utils.showSpinner();

        return utils.performRequest("/api/widgets")
            .then(resp => resp.json())
            .then(resp => setWidgets((resp.result ?? [])?.map(w => {
                return {
                    i: utils.generateUUID(),
                    dbId: w.id,
                    name: w.name,
                    type: w.type,
                    x: w.positionX,
                    y: w.positionY,
                    h: w.sizeY,
                    w: w.sizeX,
                    parameters: w.parameters
                }
            }) ?? []))
            .then(r => {
                utils.hideSpinner();
                showUnsavedLayoutWarning(false);
            });
    }

    function openWidgetCreationModal() {
        setEditedWidget({
            i: utils.generateUUID(),
            dbId: undefined,
            x: 0,
            y: 0,
            w: 2,
            h: 2
        });

        openWidgetModal(true);
    }

    function openWidgetEditModal(widget) {
        setEditedWidget({...widget});
        openWidgetModal(true);
    }

    async function createNewWidget(widget) {
        utils.showSpinner();

        await utils.performRequest("/api/widgets", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    positionX: widget.x,
                    positionY: widget.y,
                    sizeX: widget.w,
                    sizeY: widget.h,
                    name: widget.name,
                    type: widget.type,
                    parameters: widget.parameters
                })
            })
            .then(resp => resp.json())
            .then(resp => {
                widget.dbId = resp.result.createdId;
                setWidgets([...widgets, {...widget}]);
                openWidgetModal(false);
                utils.hideSpinner();
            });
    }

    function saveWidgetsLayout() {
        utils.showSpinner();

        let promises = widgets.map(w => {
            return utils.performRequest(`/api/widgets/${w.dbId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    positionX: w.x,
                    positionY: w.y,
                    sizeX: w.w,
                    sizeY: w.h,
                    name: w.name,
                    type: w.type,
                    parameters: w.parameters
                })
            })
        });

        toast.promise(
            Promise.resolve(promises)
                //.then(r => fetchWidgets())
                .then(r => {
                    utils.hideSpinner();
                    showUnsavedLayoutWarning(false);
                }),
            {
                loading: "Saving...",
                success: "Saved",
                error: (err) => `Uh oh, something went wrong!: ${err}`
            }
        );
    }

    async function updateExistingWidget(widget) {
        utils.showSpinner();
        openWidgetModal(false);

        await utils.performRequest(`/api/widgets/${widget.dbId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                positionX: widget.x,
                positionY: widget.y,
                sizeX: widget.w,
                sizeY: widget.h,
                name: widget.name,
                type: widget.type,
                parameters: widget.parameters
            })
        })
            .then(resp => resp.json())
            .then(r => fetchWidgets())
            .then(resp => {
                utils.hideSpinner();
            });
    }

    function removeWidget() {
        utils.showSpinner();

        toast.promise(
            utils.performRequest(`/api/widgets/${removingWidgetId}`, { method: "DELETE" })
                .then(resp => fetchWidgets())
                .then(resp => utils.hideSpinner())
                .then(resp => showRemoveWidgetDialog(false)),
            {
                loading: "Deleting...",
                success: "Deleted",
                error: (err) => `Uh oh, something went wrong!: ${err}`
            }
        );
    }

    function saveLayout() {

    }

    function setEditedWidgetState(widget) {
        setEditedWidget({...widget})

        // setWidgets([...widgets.filter(w => w.id !== widget.id), {...widget}]);
    }

    return (
        <Stack>
            <Grid container spacing={1}>
                <Grid container xs={12} lg={12}>
                    <Grid xs={1} lg={1}>
                        <Button
                            sx={{
                                width: "100%"
                            }}
                            variant="contained"
                            onClick={openWidgetCreationModal}
                            startIcon={<Addchart/>}
                        >
                            Add Widget
                        </Button>
                    </Grid>
                    <Grid xs={1} lg={1}>
                        <Button
                            sx={{
                                width: "100%"
                            }}
                            variant={isUnsavedLayoutWarningShown ? "contained" : "outlined"}
                            onClick={saveWidgetsLayout}
                            startIcon={<Save/>}
                        >
                            Save Layout
                        </Button>
                    </Grid>
                </Grid>
                <Grid xs={12} lg={12}>
                    <ResponsiveGridLayout
                        onLayoutChange={layout => {
                            let newWidgets = layout.map(l => {
                                let widget = widgets.find(w => w.i === l.i);

                                widget.x = l.x;
                                widget.y = l.y;
                                widget.w = l.w;
                                widget.h = l.h;

                                return widget;
                            })

                            setWidgets([...newWidgets])
                            console.log("layout change")
                        }}
                        draggableCancel=".grid-drag-cancel"
                        cols={{lg: 12, md: 10, sm: 6, xs: 4, xxs: 2}}
                        breakpoints={{lg: 1200, md: 996, sm: 768, xs: 480, xxs: 0}}
                    >
                        {
                            widgets.map((item) => (
                                <div
                                    key={item.i}
                                    data-grid={{
                                        x: item.x,
                                        y: item.y,
                                        w: item.w,
                                        h: item.h,
                                        resizeHandles: ["se"]
                                    }}
                                >
                                    <WidgetContainer
                                        widget={item}
                                        onEdit={(e) => {
                                            setEditedWidget({...item});
                                            openWidgetModal(true);
                                        }}
                                        onRemove={(e) => {
                                            setRemovingWidgetId(item.dbId);
                                            showRemoveWidgetDialog(true);
                                        }}
                                    />
                                </div>
                            ))
                        }
                    </ResponsiveGridLayout>
                </Grid>
            </Grid>

            <WidgetEditModal
                initialWidget={editedWidget}
                onCreate={createNewWidget}
                onEdit={updateExistingWidget}
                open={isWidgetModalOpen}
                setOpen={openWidgetModal}
            />

            <Dialog
                open={isRemoveWidgetDialogShown}
            >
                <DialogTitle id="delete-widget-dialog-title">
                    Delete This Widget?
                </DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Deleting a widget will permanently remove it from your dashboard and all parameters set for it will be lost.
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button
                        onClick={removeWidget}
                    >
                        Delete
                    </Button>
                    <Button
                        onClick={() => showRemoveWidgetDialog(false)}
                        autoFocus
                        variant="contained"
                    >
                        Cancel
                    </Button>
                </DialogActions>
            </Dialog>
        </Stack>
    );
}
