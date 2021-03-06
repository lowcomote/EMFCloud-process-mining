/********************************************************************************
 * Copyright (c) 2019-2021 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
import { GLSPDiagramLanguage } from "@eclipse-glsp/theia-integration";

export const EcoreLanguage: GLSPDiagramLanguage = {
    contributionId: "ecore",
    label: "Ecore Diagram",
    diagramType: "ecorediagram",
    fileExtensions: [".ecore"],
    iconClass: "codicon codicon-type-hierarchy-sub file-icon"
};
