package com.vonage.confighelper.service

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent
import com.intellij.openapi.vfs.newvfs.events.VFileCopyEvent
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileDeleteEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent
import com.intellij.util.messages.MessageBusConnection

@Service(Service.Level.PROJECT)
class FileChangeListener(
    private val project: Project
) {

    private var connection: MessageBusConnection? = null
    private val listeners = mutableListOf<(VirtualFile, ChangeType) -> Unit>()

    enum class ChangeType {
        CREATED,
        MODIFIED,
        DELETED,
        MOVED,
        COPIED,
    }

    init {
        startListening()
    }

    private fun startListening() {
        connection = project.messageBus.connect()
        connection?.subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
            override fun after(events: List<VFileEvent>) {
                events.forEach { event ->
                    handleFileEvent(event)
                }
            }
        })
    }

    private fun handleFileEvent(event: VFileEvent) {
        val file = event.file ?: return

        when (event) {
            is VFileCreateEvent -> {
                notifyListeners(file, ChangeType.CREATED)
            }

            is VFileContentChangeEvent -> {
                notifyListeners(file, ChangeType.MODIFIED)
            }

            is VFileDeleteEvent -> {
                notifyListeners(file, ChangeType.DELETED)
            }

            is VFileMoveEvent -> {
                notifyListeners(file, ChangeType.MOVED)
            }

            is VFileCopyEvent -> {
                val newFile = event.findCreatedFile() ?: return
                notifyListeners(newFile, ChangeType.COPIED)
            }
        }
    }

    fun addListener(listener: (VirtualFile, ChangeType) -> Unit) {
        listeners.add(listener)
    }

    fun removeListener(listener: (VirtualFile, ChangeType) -> Unit) {
        listeners.remove(listener)
    }

    private fun notifyListeners(file: VirtualFile, changeType: ChangeType) {
        listeners.forEach { it(file, changeType) }
    }

    fun dispose() {
        connection?.disconnect()
        listeners.clear()
    }
}
