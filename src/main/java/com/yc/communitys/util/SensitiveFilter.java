package com.yc.communitys.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词过滤器，使用前缀树实现
 * 1 定义前缀树
 * 2 根据敏感词, 初始化前缀树
 * 3 编写过滤敏感词的方法
 */
@Component
public class SensitiveFilter {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    /**
     * 替换符
     */
    private static final String REPLACEMENT = "***";

    /**
     * 根节点
     */
    private TrieNode rootNode = new TrieNode();

    /**
     * 这个 Bean即SensitiveFilter 在程序启动时就实例化,
     * 然后调用构造器,
     * 之后这个方法就会被调用.
     * this.getClass().getClassLoader() : 类加载器是从类路径下加载资源, 也就是 target/classes
     * 在 getResourceAsStream("sensitive-words.txt") 直接写文件名即可
     */
    @PostConstruct
    public void init() {
        try(
                // 初始化输入流并转换为缓冲流
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                // 此处体现了装饰器模式
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 将一个敏感词添加到前缀树中
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败: " + e.getMessage());
        }
    }

    /**
     * 将一个敏感词添加到前缀树中
     *
     * @param keyword 敏感词
     */
    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode; // tempNode指向根节点, 相当于指针
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            // 如果根节点没有子节点, 则初始化并添加
            if (subNode == null) {
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            // 指向子节点, 进入下一轮
            tempNode = subNode;

            // 设置结束标识
            if (i == keyword.length()-1) {
                tempNode.setKeywordEnd(true);
            }
        }

    }

    /**
     * 过滤敏感词
     * 对于敏感词中有符号的先去除符号
     * 需要三个指针
     * 还需要跳过特殊符号
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    /*
    public String filter(String text) {
        // 先判断是否为空
        if (StringUtils.isBlank(text)) {
            return null;
        }

        // 指针1
        TrieNode tempNode = rootNode;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;
        // 结果
        StringBuilder sb = new StringBuilder();

        // 检测: 默认处于根节点, 所以检测它的下一级
        while (position < text.length()) {
            char c = text.charAt(position);

            // 跳过符号: 指针向下走一步
            if (isSymbol(c)) {
                // 若指针1处于根节点，将此符号计入结果，让指针2向下走一步
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                // 无论符号在开头或者中间，指针3都向下走一步
                position++;
                continue;
            }

            // 若不是符号: 检查下级节点:
            tempNode = tempNode.getSubNode(c); // 指针指向下级节点
            if (tempNode == null) { // 下级没有结点
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // 进入下一个位置
                position = ++begin;
                // 归位: 重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                // 发现敏感词，将begin~position字符串替换掉
                sb.append(REPLACEMENT);
                // 进入下一个位置
                begin = ++position;
                // 重新指向根节点
                tempNode = rootNode;
            } else {
                // 检查下一个字符
                position++;
            }
        } // while 结束

        // 将最后一批字符计入结果: 指针3 提前到终点, 但 指针2 并未到终点.
        sb.append(text.substring(begin));

        return sb.toString();
    }
    */
    public String filter(String text) {
        //若是空字符串 返回空
        if (StringUtils.isBlank(text)) {
            return null;
        }
        // 根节点
        // 每次在目标字符串中找到一个敏感词，完成替换之后，都要再次从根节点遍历树开始一次新的过滤
        TrieNode tempNode = rootNode;
        // begin指针作用是目标字符串每次过滤的开头
        int begin = 0;
        // position指针的作用是指向待过滤的字符
        // 若position指向的字符是敏感词的结尾，那么text.subString(begin,position+1)就是一个敏感词
        int position = 0;
        //过滤后的结果
        StringBuilder result = new StringBuilder();

        //开始遍历 position移动到目标字符串尾部是 循环结束
        while (position < text.length()) {
            // 最开始时begin指向0 是第一次过滤的开始
            // position也是0
            char c = text.charAt(position);

            //忽略用户故意输入的符号 例如嫖※娼 忽略※后 前后字符其实也是敏感词
            if (isSymbol(c)) {
                //判断当前节点是否为根节点
                //若是根节点 则代表目标字符串第一次过滤或者目标字符串中已经被遍历了一部分
                //因为每次过滤掉一个敏感词时，都要将tempNode重新置为根节点,以重新去前缀树中继续过滤目标字符串剩下的部分
                //因此若是根节点，代表依次新的过滤刚开始，可以直接将该特殊符号字符放入到结果字符串中
                if (tempNode == rootNode) {
                    //将用户输入的符号添加到result中
                    result.append(c);
                    //此时将单词begin指针向后移动一位，以开始新的一个单词过滤
                    begin++;
                }
                //若当前节点不是根节点，那说明符号字符后的字符还需要继续过滤
                //所以单词开头位begin不变化，position向后移动一位继续过滤
                position++;
                continue;
            }
            //判断当前节点的子节点是否有目标字符c
            tempNode = tempNode.getSubNode(c);
            //如果没有 代表当前beigin-position之间的字符串不是敏感词
            // 但begin+1-position却不一定不是敏感词
            if (tempNode == null) {
                //所以只将begin指向的字符放入过滤结果
                result.append(text.charAt(begin));
                //position和begin都指向begin+1
                position = ++begin;
                //再次过滤
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                //如果找到了子节点且子节点是敏感词的结尾
                //则当前begin-position间的字符串是敏感词 将敏感词替换掉
                result.append(REPLACEMENT);
                //begin移动到敏感词的下一位
                begin = ++position;
                //再次过滤
                tempNode = rootNode;
                //&& begin < position - 1
            } else if (position + 1 == text.length()) {
                //特殊情况
                //虽然position指向的字符在树中存在，但不是敏感词结尾，并且position到了目标字符串末尾（这个重要）
                //因此begin-position之间的字符串不是敏感词 但begin+1-position之间的不一定不是敏感词
                //所以只将begin指向的字符放入过滤结果
                result.append(text.charAt(begin));
                //position和begin都指向begin+1
                position = ++begin;
                //再次过滤
                tempNode = rootNode;
            } else {
                //position指向的字符在树中存在，但不是敏感词结尾，并且position没有到目标字符串末尾
                position++;
            }
        }
        return begin < text.length() ? result.append(text.substring(begin)).toString() : result.toString();
    }

    /**
     * 判断是否是符号
     * CharUtils.isAsciiAlphanumeric 方法可以判断是否是 合法字符,
     * 取反表示 它不是合法字符, 即它是特殊符号
     * 0x2E80 到 0x9FFF为东亚文字, 不认为是符号
     */
    private boolean isSymbol(char c) {
        // 既不是字母数字也不是东亚文字 (0x2E80 到 0x9FFF为东亚文字), 可以认为是符号
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    /**
     * 定义前缀树节点
     */
    private class TrieNode {

        // 关键词结束标志
        private boolean isKeywordEnd = false;

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // 子节点 (key是下级字符, value是下级节点)
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        /**
         * 添加子节点
         *
         * @param c    字符
         * @param node 前缀树节点
         */
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        /**
         * 获取子节点
         *
         * @param c 字符
         * @return 子节点的引用
         */
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }

    }

}
