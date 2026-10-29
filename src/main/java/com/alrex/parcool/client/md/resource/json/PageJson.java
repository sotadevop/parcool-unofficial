package com.alrex.parcool.client.md.resource.json;

import com.alrex.parcool.ParCool;
import net.minecraft.resources.Identifier;

public class PageJson {
    private static final Identifier DEFAULT = ParCool.resourceLocation("not_found");
    public String title = "parcool.guide.pageId.not_found";
    public Identifier location = DEFAULT;
}
