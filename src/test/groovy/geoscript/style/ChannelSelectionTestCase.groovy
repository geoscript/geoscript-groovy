package geoscript.style

import org.junit.Test
import static org.junit.Assert.*

/**
 * The ChannelSelection UnitTest
 * @author Jared Erickson
 */
class ChannelSelectionTestCase {

    @Test void constructors() {

        // RGB Names
        def channel = new ChannelSelection("red", "green", "blue")
        assertEquals "red", channel.redName
        assertEquals "green", channel.greenName
        assertEquals "blue", channel.blueName

        // Gray Name
        channel = new ChannelSelection("gray")
        assertEquals "gray", channel.grayName

        // Empty Constructor RGB
        channel = new ChannelSelection()
            .red("red", new ContrastEnhancement("histogram", 0.35))
            .green("green", new ContrastEnhancement("histogram", 0.45))
            .blue("blue", new ContrastEnhancement("histogram", 0.55))

        assertEquals "red", channel.redName
        assertEquals "histogram", channel.redContrastEnhancement.method
        assertEquals 0.35, channel.redContrastEnhancement.gammaValue.value

        assertEquals "green", channel.greenName
        assertEquals "histogram", channel.greenContrastEnhancement.method
        assertEquals 0.45, channel.greenContrastEnhancement.gammaValue.value

        assertEquals "blue", channel.blueName
        assertEquals "histogram", channel.blueContrastEnhancement.method
        assertEquals 0.55, channel.blueContrastEnhancement.gammaValue.value

        // Empty Constructor Gray
        channel = new ChannelSelection().gray("gray", new ContrastEnhancement("normalize"))
        assertEquals "gray", channel.grayName
        assertEquals "normalize", channel.grayContrastEnhancement.method
        assertNull channel.grayContrastEnhancement.gammaValue
    }

    @Test void apply() {

        // RGB Names
        def channel = new ChannelSelection("red", "green", "blue")
        def sym = Symbolizer.styleFactory.createRasterSymbolizer()
        channel.apply(sym)
        assertEquals "red", sym.channelSelection.RGBChannels[0].channelName.value
        assertEquals "green", sym.channelSelection.RGBChannels[1].channelName.value
        assertEquals "blue", sym.channelSelection.RGBChannels[2].channelName.value

        // Gray Name
        channel = new ChannelSelection("gray")
        sym = Symbolizer.styleFactory.createRasterSymbolizer()
        channel.apply(sym)
        assertEquals "gray", sym.channelSelection.grayChannel.channelName.value

        // Empty Constructor RGB
        channel = new ChannelSelection()
            .red("red", new ContrastEnhancement("histogram", 0.35))
            .green("green", new ContrastEnhancement("histogram", 0.45))
            .blue("blue", new ContrastEnhancement("histogram", 0.55))
        sym = Symbolizer.styleFactory.createRasterSymbolizer()
        channel.apply(sym)

        assertEquals "red", sym.channelSelection.RGBChannels[0].channelName.value
        assertEquals "HISTOGRAM", sym.channelSelection.RGBChannels[0].contrastEnhancement.method.name()
        assertEquals 0.35, sym.channelSelection.RGBChannels[0].contrastEnhancement.gammaValue.value

        assertEquals "green", sym.channelSelection.RGBChannels[1].channelName.value
        assertEquals "HISTOGRAM", sym.channelSelection.RGBChannels[1].contrastEnhancement.method.name()
        assertEquals 0.45, sym.channelSelection.RGBChannels[1].contrastEnhancement.gammaValue.value

        assertEquals "blue", sym.channelSelection.RGBChannels[2].channelName.value
        assertEquals "HISTOGRAM", sym.channelSelection.RGBChannels[2].contrastEnhancement.method.name()
        assertEquals 0.55, sym.channelSelection.RGBChannels[2].contrastEnhancement.gammaValue.value

        // Empty Constructor Gray
        channel = new ChannelSelection().gray("gray", new ContrastEnhancement("normalize"))
        sym = Symbolizer.styleFactory.createRasterSymbolizer()
        channel.apply(sym)
        assertEquals "gray", sym.channelSelection.grayChannel.channelName.value
        assertEquals "NORMALIZE", sym.channelSelection.grayChannel.contrastEnhancement.method.name()
        assertNull sym.channelSelection.grayChannel.contrastEnhancement.gammaValue
    }

    @Test void prepare() {

        // RGB Names
        def channel = new ChannelSelection("red", "green", "blue")
        def rule = Symbolizer.styleFactory.createRule()
        rule.symbolizers().add(Symbolizer.styleFactory.createRasterSymbolizer())
        channel.prepare(rule)
        def sym = rule.symbolizers[0]
        assertEquals "red", sym.channelSelection.RGBChannels[0].channelName.value
        assertEquals "green", sym.channelSelection.RGBChannels[1].channelName.value
        assertEquals "blue", sym.channelSelection.RGBChannels[2].channelName.value
    }

    @Test void string() {

         // RGB Names
        def channel = new ChannelSelection("red", "green", "blue")
        assertEquals "ChannelSelection(redName = red, greenName = green, blueName = blue)", channel.toString()

        // Gray Name
        channel = new ChannelSelection("gray")
        assertEquals "ChannelSelection(grayName = gray)", channel.toString()

        // Empty Constructor RGB
        channel = new ChannelSelection()
            .red("red", new ContrastEnhancement("histogram", 0.35))
            .green("green", new ContrastEnhancement("histogram", 0.45))
            .blue("blue", new ContrastEnhancement("histogram", 0.55))
        assertEquals "ChannelSelection(redName = red, redContrastEnhancement = ContrastEnhancement(method = histogram, gammaValue = 0.35), greenName = green, greenContrastEnhancement = ContrastEnhancement(method = histogram, gammaValue = 0.45), blueName = blue, blueContrastEnhancement = ContrastEnhancement(method = histogram, gammaValue = 0.55))", channel.toString()

        // Empty Constructor Gray
        channel = new ChannelSelection().gray("gray", new ContrastEnhancement("normalize"))
        assertEquals "ChannelSelection(grayName = gray, grayContrastEnhancement = ContrastEnhancement(method = normalize))", channel.toString()
    }

}
