package migration

import net.praqma.clearcase.ucm.view.SnapshotView

class ClearCaseOptions {
    SnapshotView.Components loadComponents = SnapshotView.Components.MODIFIABLE
    String migrationProject = null
    boolean readOnlyMigrationStream = false
}