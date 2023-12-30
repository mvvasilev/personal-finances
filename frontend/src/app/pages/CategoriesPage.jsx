import {
    Category as CategoryIcon,
    Add as AddIcon,
    Close as CloseIcon,
    Save as SaveIcon
} from "@mui/icons-material";
import {useEffect, useState} from "react";
import utils from "@/utils.js";
import toast from "react-hot-toast";
import Grid from "@mui/material/Unstable_Grid2";
import Button from "@mui/material/Button";
import Divider from "@mui/material/Divider";
import {
    Chip,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
    Modal,
    Stack,
    TextField
} from "@mui/material";
import Box from "@mui/material/Box";
import CategorizationRulesEditor from "@/components/categories/CategorizationRulesEditor.jsx";
import CategoriesBox from "@/components/categories/CategoriesBox.jsx";

export default function CategoriesPage() {

    const [categories, setCategories] = useState([]);
    const [selectedCategory, setSelectedCategory] = useState(null);
    const [isCategoryModalOpen, openCategoryModal] = useState(false);
    const [showConfirmDeleteCategoryModal, openConfirmDeleteCategoryModal] = useState(false);
    const [showApplyRulesConfirmModal, openApplyRulesConfirmModal] = useState(false);
    const [newCategoryName, setNewCategoryName] = useState(null);

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

    return (
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
                <Grid xs={10} lg={10}></Grid>
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
                {/*<Stack*/}
                {/*    sx={{*/}
                {/*        overflowY: "scroll"*/}
                {/*    }}*/}
                {/*    minHeight={"100px"}*/}
                {/*    maxHeight={"250px"}*/}
                {/*    useFlexGap*/}
                {/*    flexWrap="wrap"*/}
                {/*    direction={"row"}*/}
                {/*    spacing={1}*/}
                {/*>*/}
                {/*    {*/}
                {/*        categories.map(c => {*/}
                {/*            let variant = (selectedCategory?.id ?? -1) === c.id ? "filled" : "outlined";*/}

                {/*            return (*/}
                {/*                <Chip*/}
                {/*                    key={c.id}*/}
                {/*                    onClick={(e) => {*/}
                {/*                        setSelectedCategory({...c});*/}
                {/*                    }}*/}
                {/*                    onDelete={() => {*/}
                {/*                        setSelectedCategory(c);*/}
                {/*                        openConfirmDeleteCategoryModal(true);*/}
                {/*                    }}*/}
                {/*                    label={c.name}*/}
                {/*                    deleteIcon={<Delete/>}*/}
                {/*                    variant={variant}*/}
                {/*                />*/}
                {/*            );*/}
                {/*        })*/}
                {/*    }*/}
                {/*</Stack>*/}
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

            <Modal
                sx={{
                    position: "absolute",
                    top: "50%",
                    left: "50%",
                    transform: 'translate(-50%, -50%)',
                    width: 400,
                    height: "fit-content",
                    p: 4
                }}
                open={isCategoryModalOpen}
            >
                <Box>
                    <h3>Create New Category</h3>
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
                </Box>
            </Modal>
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
        </Grid>
    );
}