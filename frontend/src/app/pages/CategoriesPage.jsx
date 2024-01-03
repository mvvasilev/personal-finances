import {
    Category as CategoryIcon,
    Add as AddIcon,
    Close as CloseIcon,
    Save as SaveIcon, Download, Upload
} from "@mui/icons-material";
import {useEffect, useState} from "react";
import utils from "@/utils.js";
import toast from "react-hot-toast";
import Grid from "@mui/material/Unstable_Grid2";
import Button from "@mui/material/Button";
import Divider from "@mui/material/Divider";
import {
    Backdrop,
    Checkbox,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle, FormControlLabel,
    Modal,
    TextField
} from "@mui/material";
import Box from "@mui/material/Box";
import CategorizationRulesEditor from "@/components/categories/CategorizationRulesEditor.jsx";
import CategoriesBox from "@/components/categories/CategoriesBox.jsx";
import {PARAMS} from "@/components/widgets/WidgetParameters.js";
import * as React from "react";
import VisuallyHiddenInput from "@/components/VisuallyHiddenInput.jsx";
import Card from "@mui/material/Card";
import Typography from "@mui/material/Typography";

export default function CategoriesPage() {

    const [categories, setCategories] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState(null);
    const [isCategoryModalOpen, openCategoryModal] = useState(false);
    const [showConfirmDeleteCategoryModal, openConfirmDeleteCategoryModal] = useState(false);
    const [showApplyRulesConfirmModal, openApplyRulesConfirmModal] = useState(false);
    const [newCategoryName, setNewCategoryName] = useState(null);
    const [showUploadDialog, openUploadDialog] = useState(false);
    const [replaceExistingOnUpload, setReplaceExistingOnUpload] = useState(true);

    useEffect(() => {
        utils.showSpinner();

        toast.promise(
            fetchCategories(),
            {
                loading: "Loading...",
                success: () => {
                    utils.hideSpinner();

                    return "Ready";
                },
                error: (err) => {
                    utils.hideSpinner();

                    return `Uh oh! Something went wrong: ${err}`;
                }
            }
        )
    }, []);

    function fetchCategories() {
        return utils.performRequest("/api/categories")
            .then(resp => resp.json())
            .then(({result}) => setCategories(result));
    }

    function createNewCategory() {
        utils.showSpinner();

        toast.promise(
            utils.performRequest("/api/categories", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    name: newCategoryName
                })
            }).then(resp => fetchCategories()),
            {
                loading: "Saving...",
                success: () => {
                    openCategoryModal(false);
                    utils.hideSpinner();

                    return "Saved";
                },
                error: (err) => {
                    openCategoryModal(false);
                    utils.hideSpinner();

                    return `Uh oh! Something went wrong: ${err}`;
                }
            }
        );
    }

    function deleteSelectedCategory() {
        utils.showSpinner();

        toast.promise(
            utils.performRequest(`/api/categories/${selectedCategory.id}`, {
                method: "DELETE"
            }).then(resp => fetchCategories()),
            {
                loading: "Deleting...",
                success: () => {
                    openConfirmDeleteCategoryModal(false);
                    setSelectedCategory(null);
                    utils.hideSpinner();

                    return "Deleted";
                },
                error: (err) => {
                    openConfirmDeleteCategoryModal(false);
                    setSelectedCategory(null);
                    utils.hideSpinner();

                    return `Uh oh! Something went wrong: ${err}`;
                }
            }
        );
    }

    function applyCategorizationRules() {
        utils.showSpinner();

        toast.promise(
            utils.performRequest(`/api/categories/categorize`, {
                method: "POST"
            }),
            {
                loading: "Deleting...",
                success: () => {
                    openApplyRulesConfirmModal(false);
                    utils.hideSpinner();

                    return "Applied";
                },
                error: (err) => {
                    openApplyRulesConfirmModal(false);
                    utils.hideSpinner();

                    return `Uh oh! Something went wrong: ${err}`;
                }
            }
        );
    }

    function saveCategory(category) {
        utils.performRequest(`/api/categories/${category.id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                ...category,
                ruleBehavior: category.ruleBehavior ?? "ANY",
            })
        });
    }

    function downloadCategories() {
        toast.promise(
            utils.performRequest("/api/categories/export")
                .then(resp => resp.json())
                .then(resp => {
                    const linkSource = `data:application/json;base64,${resp.result}`;
                    const downloadLink = document.createElement("a");
                    const fileName = "categories.json";

                    downloadLink.href = linkSource;
                    downloadLink.download = fileName;
                    downloadLink.click();
                }),
            {
                loading: "Exporting...",
                success: "Exported",
                error: (err) => `Uh oh! Something went wrong: ${err}`
            }
        )
    }

    function uploadCategories(e) {
        utils.showSpinner();

        if (!e.target.files) {
            return;
        }

        const file = e.target.files[0];

        const formData = new FormData();
        formData.append("file", file);
        formData.append("deleteExisting", replaceExistingOnUpload);

        toast.promise(
            utils.performRequest("/api/categories/import", {
                    method: "POST",
                    body: formData
                })
                .then(resp => fetchCategories())
                .then(resp => {
                    setSelectedCategory(null);
                    openUploadDialog(false);
                    utils.hideSpinner();
                }),
            {
                loading: "Importing...",
                success: "Imported",
                error: (err) => {
                    utils.hideSpinner();
                    openUploadDialog(false);

                    return `Uh oh! Something went wrong: ${err}`;
                }
            }
        );
    }

    return (
        <div>
            <Grid container spacing={1}>

                <Grid container xs={12} lg={12}>
                    <Grid xs={1} lg={1}>
                        <Button sx={{ width:"100%" }} variant="contained" startIcon={<AddIcon />} onClick={() => openCategoryModal(true)}>
                            Add Category
                        </Button>
                    </Grid>
                    <Grid xs={1} lg={1}>
                        <Button sx={{ width:"100%" }} variant="outlined" startIcon={<CategoryIcon />} onClick={() => openApplyRulesConfirmModal(true)}>
                            Apply Rules
                        </Button>
                    </Grid>
                    <Grid xs={1} lg={1}>
                        <Button sx={{ width:"100%" }} variant="outlined" startIcon={<Download />} onClick={() => downloadCategories()}>
                            Export
                        </Button>
                    </Grid>
                    <Grid xs={1} lg={1}>
                        <Button sx={{ width:"100%" }} variant="outlined" startIcon={<Upload />} onClick={() => openUploadDialog(true)}>
                            Import
                        </Button>
                    </Grid>
                    <Grid xs={8} lg={8}></Grid>
                </Grid>

                <Grid xs={12} lg={12}>
                    <CategoriesBox
                        categories={categories}
                        minHeight={"100px"}
                        maxHeight={"250px"}
                        selectable
                        selected={selectedCategory}
                        onCategorySelect={(e, c) => setSelectedCategory({...c})}
                        onCategoryDelete={(e, c) => {
                            setSelectedCategory(c);
                            openConfirmDeleteCategoryModal(true);
                        }}
                        showDelete
                    />
                </Grid>

                <Grid xs={12} lg={12}>
                    <Divider></Divider>
                </Grid>

                <Grid xs={12} lg={12}>
                    {
                        selectedCategory &&
                        <CategorizationRulesEditor
                            selectedCategory={selectedCategory}
                            onRuleBehaviorSelect={(value) => {
                                selectedCategory.ruleBehavior = value;
                                setSelectedCategory({...selectedCategory});
                            }}
                            onSave={() => saveCategory(selectedCategory)}
                        />
                    }
                </Grid>
                <Dialog
                    open={showConfirmDeleteCategoryModal}
                >
                    <DialogTitle id="delete-category-dialog-title">
                        {`Delete Category "${selectedCategory?.name}"?`}
                    </DialogTitle>
                    <DialogContent>
                        <DialogContentText>
                            Deleting a category will also clear it from all transactions it is currently applied to
                        </DialogContentText>
                    </DialogContent>
                    <DialogActions>
                        <Button
                            onClick={deleteSelectedCategory}
                        >
                            Delete
                        </Button>
                        <Button
                            onClick={() => openConfirmDeleteCategoryModal(false)}
                            autoFocus
                            variant="contained"
                        >
                            Cancel
                        </Button>
                    </DialogActions>
                </Dialog>
                <Dialog
                    open={showApplyRulesConfirmModal}
                >
                    <DialogTitle id="apply-rules-dialog-title">
                        {"Apply all categorization rules?"}
                    </DialogTitle>
                    <DialogContent>
                        <DialogContentText>
                            Applying all categorization rules to your current transactions will wipe all categories
                            assigned to them, and re-assign them based on the rules as currently defined.
                        </DialogContentText>
                    </DialogContent>
                    <DialogActions>
                        <Button
                            onClick={applyCategorizationRules}
                        >
                            Apply
                        </Button>
                        <Button
                            onClick={() => openApplyRulesConfirmModal(false)}
                            autoFocus
                            variant="contained"
                        >
                            Cancel
                        </Button>
                    </DialogActions>
                </Dialog>
                <Dialog
                    open={showUploadDialog}
                >
                    <DialogTitle id="upload-dialog-title">
                        {"Replace Existing Categories?"}
                    </DialogTitle>
                    <DialogContent>
                        <DialogContentText>
                            Would you like to replace your existing categories completely with the ones in your import?
                            ( Note that this will remove all current categories from your transactions )
                        </DialogContentText>
                        <FormControlLabel
                            sx={{ width: "100%", height: "100%" }}
                            value="end"
                            control={
                                <Checkbox
                                    checked={replaceExistingOnUpload}
                                    onChange={(e) => {
                                        setReplaceExistingOnUpload(e.target.checked);
                                    }}
                                />
                            }
                            label="Replace Existing Categories"
                            labelPlacement="end"
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button
                            component="label"
                        >
                            <VisuallyHiddenInput type="file" onChange={uploadCategories}/>
                            Select File
                        </Button>
                        <Button
                            onClick={() => openUploadDialog(false)}
                            autoFocus
                            variant="contained"
                        >
                            Cancel
                        </Button>
                    </DialogActions>
                </Dialog>
            </Grid>
            <Modal
                open={isCategoryModalOpen}
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
                    <Typography sx={{ pb: 1, fontSize: "1.25em"}}>Create New Category</Typography>
                    <Divider></Divider>
                    <Grid container spacing={1}>
                        <Grid xs={12} lg={12}>
                            <TextField
                                id="category-name"
                                label="Category Name"
                                variant="outlined"
                                onChange={(e) => setNewCategoryName(e.target.value)}
                                autoFocus
                                sx={{width: "100%"}}
                            />
                        </Grid>
                        <Grid xs={6} lg={6}>
                            <Button
                                sx={{width: "100%"}}
                                variant="contained"
                                onClick={createNewCategory}
                                startIcon={<SaveIcon />}
                            >
                                Create
                            </Button>
                        </Grid>
                        <Grid xs={6} lg={6}>
                            <Button
                                sx={{width: "100%"}}
                                onClick={() => openCategoryModal(false)}
                                startIcon={<CloseIcon />}
                            >
                                Cancel
                            </Button>
                        </Grid>
                    </Grid>
                </Card>
            </Modal>
        </div>
    );
}