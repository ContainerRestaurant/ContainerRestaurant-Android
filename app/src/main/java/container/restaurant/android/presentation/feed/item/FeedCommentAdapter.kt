package container.restaurant.android.presentation.feed.item

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.dino.library.dinorecyclerview.DinoAdapter
import com.dino.library.dinorecyclerview.DinoViewHolder
import container.restaurant.android.R
import container.restaurant.android.databinding.ItemCommentReplyBinding
import container.restaurant.android.util.RecyclerViewItemClickListeners

class FeedCommentAdapter(
    val replyItemClickListener: RecyclerViewItemClickListeners.ReplyItemClickListener,
    val feedCommentList: LiveData<List<CommentReplyItem>>
): DinoAdapter(R.layout.item_comment_reply) {
    inner class FeedCommentViewHolder(@LayoutRes layoutResId: Int, parent: ViewGroup?): DinoViewHolder(layoutResId, parent) {
        lateinit var feedDetailCommentView : View
        lateinit var rvFeedDetailReply : RecyclerView
        override fun onBindViewHolder(item: Any?, eventHolder: Any?) {
            super.onBindViewHolder(item, eventHolder)
            val rvReplyAdapter = DinoAdapter(R.layout.item_reply).apply {
                this.eventHolder = replyItemClickListener
                val comment = feedCommentList.value?.get(adapterPosition)?.commentReply
                val replyItemList = mutableListOf<ReplyItem>().apply {
                    comment?.replyList?.forEach { reply ->
                        add(ReplyItem(reply))
                    }
                }
                replaceAll(replyItemList)
            }
            val viewDataBinding = binding as ItemCommentReplyBinding
            viewDataBinding.rvFeedDetailReply.adapter = rvReplyAdapter
            rvFeedDetailReply = viewDataBinding.rvFeedDetailReply
            feedDetailCommentView = viewDataBinding.clFeedDetailComment
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DinoViewHolder {
        return FeedCommentViewHolder(layoutResId = viewType, parent = parent)
    }
}