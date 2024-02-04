import {Chip, Stack} from "@mui/material";
import {Delete} from "@mui/icons-material";

export default function CategoriesBox({
    categories: categories = [],
    selectable: selectable = false,
    selected: selected = {},
    onCategorySelect: onCategorySelect = (event, category) => {},
    onCategoryDelete: onCategoryDelete = undefined,
    showDelete: showDelete = false,
    sx: sx = {},
    minHeight: minHeight = "100px",
    maxHeight: maxHeight = "250px",
}) {
    let areChipsDeletable = onCategoryDelete !== undefined;

    return (
        <Stack
            sx={{
                overflowY: "scroll",
                ...sx
            }}
            minHeight={minHeight ?? "100px"}
            maxHeight={maxHeight ?? "250px"}
            useFlexGap
            flexWrap="wrap"
            direction={"row"}
            spacing={1}
        >
            {
                categories.map(c => {
                    let variant = selectable && (selected?.id ?? -1) === c.id ? "filled" : "outlined";

                    return (
                        <>
                            {
                                areChipsDeletable &&
                                <Chip
                                    key={c.id}
                                    onClick={(e) => onCategorySelect(e, c)}
                                    onDelete={(e) => onCategoryDelete(e, c)}
                                    label={c.name}
                                    deleteIcon={showDelete === true ? <Delete/> : ""}
                                    variant={variant}
                                />
                            }
                            {
                                !areChipsDeletable &&
                                <Chip
                                    key={c.id}
                                    onClick={(e) => onCategorySelect(e, c)}
                                    label={c.name}
                                    variant={variant}
                                />
                            }
                        </>
                    );
                })
            }
        </Stack>
    );
}