package com.logicalgeekboy.logical_zoom.compat;

import com.moulberry.axiom.editor.EditorUI;

public class AxiomCompat {
	public static boolean isInEditor() {
		return EditorUI.isActive();
	}
}
