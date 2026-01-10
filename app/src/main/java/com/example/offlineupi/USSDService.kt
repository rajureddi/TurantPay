package com.example.offlineupi

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.content.*
import android.os.Bundle

class USSDService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // We catch the window where the bank says "Enter UPI ID"
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {

            val text = event.text.toString()
            // If the screen asks for ID or VPA, tell the Activity
            if (text.contains("ID", true) || text.contains("VPA", true) || text.contains("UPI", true)) {
                val intent = Intent("REPLY_VPA_NOW").apply {
                    putExtra("display_text", text)
                }
                sendBroadcast(intent)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "SEND_VPA_REPLY") {
            val vpa = intent.getStringExtra("vpa_data") ?: ""
            val rootNode = rootInActiveWindow ?: return START_STICKY

            // 1. Find the Input Field
            val inputs = rootNode.findAccessibilityNodeInfosByViewId("android:id/input")
            val inputNode = if (inputs.isNotEmpty()) inputs[0] else findEditableNode(rootNode)

            inputNode?.let { node ->
                // 2. The Most Powerful Text Injection Method
                val arguments = Bundle()
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, vpa)
                node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)

                // 3. Find 'Send' button by common system ID
                val sendBtn = rootNode.findAccessibilityNodeInfosByViewId("android:id/button1").firstOrNull()
                    ?: rootNode.findAccessibilityNodeInfosByText("Send").firstOrNull()
                    ?: rootNode.findAccessibilityNodeInfosByText("SEND").firstOrNull()

                sendBtn?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
        }
        return START_STICKY
    }

    private fun findEditableNode(root: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (root.isEditable) return root
        for (i in 0 until root.childCount) {
            val child = root.getChild(i) ?: continue
            val found = findEditableNode(child)
            if (found != null) return found
        }
        return null
    }

    override fun onInterrupt() {}
}